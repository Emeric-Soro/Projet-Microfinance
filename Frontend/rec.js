const fs = require('fs');
const path = require('path');
const readline = require('readline');

async function main() {
  const logPath = 'C:\\Users\\emeso\\.gemini\\antigravity\\brain\\ba734ac8-1c49-430d-acd1-555631bd0200\\.system_generated\\logs\\transcript_full.jsonl';
  const fileStream = fs.createReadStream(logPath);
  const rl = readline.createInterface({
    input: fileStream,
    crlfDelay: Infinity
  });

  let stepCount = 0;
  let matches = [];

  for await (const line of rl) {
    stepCount++;
    if (!line) continue;
    try {
      const obj = JSON.parse(line);
      if (obj.tool_calls) {
        for (const tc of obj.tool_calls) {
          if (tc.name === 'write_to_file' || tc.name === 'replace_file_content' || tc.name === 'multi_replace_file_content') {
            const args = tc.args;
            const target = args.TargetFile || args.targetFile || '';
            if (target.includes('comptes.html')) {
              matches.push({
                step: stepCount,
                type: tc.name,
                args: args
              });
            }
          }
        }
      }
    } catch (e) {
      // ignore parse errors
    }
  }

  console.log(`Found ${matches.length} matching tool calls.`);
  // print the last 3 matches metadata
  const lastMatches = matches.slice(-3);
  for (const m of lastMatches) {
    console.log(`--- Step ${m.step} (${m.type}) ---`);
    if (m.type === 'write_to_file') {
      console.log(`Write content length: ${m.args.CodeContent?.length || 0}`);
    } else if (m.type === 'replace_file_content') {
      console.log(`Replace startLine: ${m.args.StartLine}, endLine: ${m.args.EndLine}`);
      console.log(`TargetContent prefix: ${m.args.TargetContent?.substring(0, 100)}`);
      console.log(`ReplacementContent prefix: ${m.args.ReplacementContent?.substring(0, 100)}`);
    } else {
      console.log(`Multi-replace chunks: ${m.args.ReplacementChunks?.length || 0}`);
    }
  }

  // If there are matches, write the content of the last write_to_file to a recover file
  const lastWrite = [...matches].reverse().find(m => m.type === 'write_to_file');
  if (lastWrite) {
    console.log(`Found a write_to_file at Step ${lastWrite.step}. Saving it...`);
    fs.writeFileSync('C:\\Users\\emeso\\OneDrive\\Bureau\\Projet-Microfinance\\Frontend\\pages\\comptes.html', lastWrite.args.CodeContent);
    console.log('Saved to comptes.html successfully.');
  } else {
    console.log('No write_to_file found for comptes.html.');
  }
}

main().catch(console.error);