#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script : extract_endpoints.py
Description : Extrait tous les endpoints API depuis les controllers Spring Boot
              et les écrit dans un fichier texte formaté.
Usage : python extract_endpoints.py
"""

import os
import re
import glob
from collections import OrderedDict
from datetime import datetime

CONTROLLERS_DIR = "src/main/java/com/soutra/microfinance/api/controller"
OUTPUT_FILE = "docs/all-endpoints.txt"


def extract_endpoints():
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)

    # Patterns regex compilés
    re_tag_name = re.compile(r'@Tag\(\s*name\s*=\s*"([^"]*)"')
    re_tag_desc = re.compile(r'description\s*=\s*"([^"]*)"')
    re_request_mapping = re.compile(r'@RequestMapping\("([^"]*)"\)')
    re_operation_summary = re.compile(r'@Operation\([^)]*summary\s*=\s*"([^"]*)"')

    # Mapping avec path inline : @GetMapping("/xxx")
    re_mapping_with_path = re.compile(
        r'@(Get|Post|Put|Delete|Patch)Mapping\("([^"]*)"'
    )
    # Mapping avec path sur ligne suivante : @GetMapping(\n  "xxx")
    # On détecte le @Mapping suivi de ( sans " dans la même ligne
    re_mapping_open_paren = re.compile(
        r'@(Get|Post|Put|Delete|Patch)Mapping\(\s*$'
    )
    # Mapping sans parenthèses du tout : @GetMapping
    re_mapping_bare = re.compile(
        r'@(Get|Post|Put|Delete|Patch)Mapping\s*$'
    )
    # Mapping avec parenthèse fermante vide : @GetMapping()
    re_mapping_empty_paren = re.compile(
        r'@(Get|Post|Put|Delete|Patch)Mapping\(\s*\)'
    )

    all_files = sorted(glob.glob(
        os.path.join(CONTROLLERS_DIR, "**", "*Controller.java"),
        recursive=True
    ))

    endpoints = []

    for filepath in all_files:
        classname = os.path.splitext(os.path.basename(filepath))[0]

        with open(filepath, "r", encoding="utf-8") as f:
            lines = f.readlines()
            content = "".join(lines)

        # Métadonnées controller
        tag_name = ""
        m = re_tag_name.search(content)
        if m:
            tag_name = m.group(1)

        tag_desc = ""
        m = re_tag_desc.search(content)
        if m:
            tag_desc = m.group(1)

        base_path = ""
        m = re_request_mapping.search(content)
        if m:
            base_path = m.group(1)

        # Parser ligne par ligne
        current_method = None
        current_path = None
        current_summary = ""
        waiting_for_path_after_paren = False

        for line in lines:
            stripped = line.strip()

            # --- @Operation(summary = "...") ---
            m = re_operation_summary.search(stripped)
            if m:
                current_summary = m.group(1)

            # --- Mapping avec path inline : @GetMapping("/xxx") ---
            m = re_mapping_with_path.search(stripped)
            if m:
                _flush(endpoints, tag_name, classname, tag_desc, base_path,
                       current_method, current_path, current_summary)
                current_method = m.group(1).upper()
                current_path = m.group(2)
                current_summary = ""
                waiting_for_path_after_paren = False
                continue

            # --- Mapping avec parenthèse ouvrante : @GetMapping( ---
            m = re_mapping_open_paren.match(stripped)
            if m:
                _flush(endpoints, tag_name, classname, tag_desc, base_path,
                       current_method, current_path, current_summary)
                current_method = m.group(1).upper()
                current_path = ""
                current_summary = ""
                waiting_for_path_after_paren = True
                continue

            # --- Mapping vide parenthèses : @GetMapping() ---
            m = re_mapping_empty_paren.match(stripped)
            if m:
                _flush(endpoints, tag_name, classname, tag_desc, base_path,
                       current_method, current_path, current_summary)
                current_method = m.group(1).upper()
                current_path = ""
                current_summary = ""
                waiting_for_path_after_paren = False
                continue

            # --- Mapping bare : @PostMapping (sans parenthèses) ---
            m = re_mapping_bare.match(stripped)
            if m:
                _flush(endpoints, tag_name, classname, tag_desc, base_path,
                       current_method, current_path, current_summary)
                current_method = m.group(1).upper()
                current_path = ""  # <-- base path only, pas None!
                current_summary = ""
                waiting_for_path_after_paren = False
                continue

            # --- On attend un path sur la ligne suivante ---
            if waiting_for_path_after_paren:
                if stripped.startswith('"') and stripped.rstrip('"').lstrip('"'):
                    extracted = stripped.strip('"').strip(',')
                    current_path = extracted
                    waiting_for_path_after_paren = False
                    continue
                if stripped == ")":
                    waiting_for_path_after_paren = False
                    continue

            # --- Fin de méthode Java ---
            if re.match(r'public\s+\S+\s+\w+\s*\(', stripped):
                _flush(endpoints, tag_name, classname, tag_desc, base_path,
                       current_method, current_path, current_summary)
                current_method = None
                current_path = None
                current_summary = ""
                waiting_for_path_after_paren = False

        # Flush dernier endpoint du fichier
        _flush(endpoints, tag_name, classname, tag_desc, base_path,
               current_method, current_path, current_summary)

    # --- Écriture du fichier ---
    with open(OUTPUT_FILE, "w", encoding="utf-8") as out:
        now = datetime.now().strftime("%d/%m/%Y %H:%M:%S")
        out.write("=" * 78 + "\n")
        out.write("                   CATALOGUE DES ENDPOINTS API\n")
        out.write("                   Projet Microfinance - Core Banking\n")
        out.write(f"                   Genere le : {now}\n")
        out.write("=" * 78 + "\n\n")

        grouped = OrderedDict()
        for tag, cls, desc, base, method, path, summary in endpoints:
            key = (tag or cls, cls, desc, base)
            if key not in grouped:
                grouped[key] = []
            grouped[key].append((method, path, summary))

        total = 0
        for (tag, cls, desc, base), eps in grouped.items():
            out.write("\n")
            out.write("=" * 78 + "\n")
            out.write(f"  [{tag}] {cls}\n")
            out.write(f"  Base path : {base}\n")
            if desc:
                out.write(f"  Description : {desc}\n")
            out.write("=" * 78 + "\n")
            out.write(f"  {'METHODE':<8} {'ENDPOINT':<55} {'DESCRIPTION'}\n")
            out.write("  " + "-" * 76 + "\n")

            for method, path, summary in eps:
                display_path = path if path else base
                if not display_path:
                    display_path = "/"
                out.write(f"  {method:<8} {display_path:<55} {summary}\n")
                total += 1

            out.write("\n")

        out.write("=" * 78 + "\n")
        out.write(f"  TOTAL : {total} endpoints extraits\n")
        out.write(f"  Fichier genere le {now}\n")
        out.write("=" * 78 + "\n")
    # Truncate the file to current position (in case of residual data)
    # This is handled by opening with 'w' mode above, but for extra safety:
    pass

    print(f"[OK] Fichier genere : {OUTPUT_FILE}")
    print(f"[INFO] Total endpoints : {total}")


def _flush(endpoints, tag_name, classname, tag_desc, base_path,
           current_method, current_path, current_summary):
    """Ajoute un endpoint à la liste s'il est complet."""
    if current_method is not None and current_path is not None:
        full_path = base_path + current_path
        if not full_path:
            full_path = "/"
        endpoints.append((
            tag_name, classname, tag_desc, base_path,
            current_method, full_path, current_summary
        ))


if __name__ == "__main__":
    extract_endpoints()
