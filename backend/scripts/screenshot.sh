#!/bin/bash
ADB="/mnt/c/Users/ahopa/AppData/Local/Android/Sdk/platform-tools/adb.exe"
"$ADB" shell screencap -p /sdcard/screen.png
"$ADB" pull /sdcard/screen.png /tmp/phone_screen.png
echo "Done: /tmp/phone_screen.png"
