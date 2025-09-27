#include "index.h"
void handle(){}
Promise<void> run(){/*
  const source = joinPath('.', 'index.ts');
  const target = joinPath('.', 'main.c');

  const inputBuffer = await readString(source);
  const input = inputBuffer.toString();
  const output = compile(input);
  await writeString(target, output);
}*/string[]) joinPath(){/*
  return path.join(...segments);
}*/string, output: string) writeString(){/*
  await fs.writeFile(target, output);
}*/string) readString(){/*
  return await fs.readFile(source);
}*/string): string compile(){/*
  const segments: string[] = [];
  let buffer: string[] = [];
  let depth = 0;
  for (let index = 0; index < input.length; index++) {
    const c = input[index];
    if (!c) break;

    buffer.push(c);
    if (c == ';' && depth === 0) {
      segments.push(buffer.join(''));
      buffer = [];
      continue;
    }

    if (c == '}' && depth === 1) {
      segments.push(buffer.join(''));
      buffer = [];
      depth--;
      continue;
    }

    if (c == '{') depth++;
    if (c == '}') depth--;
  }*/int main(){
	run()(handle);





/*let functions: string[] = []*/;
/*let topLevelStatements: string[] = []*/;
/*segments.forEach((segment) => {
    const compiled = compileRootSegment(segment);
    functions.push(...compiled[1]);

    topLevelStatements.push(compiled[0]);
  }*/
/*)*/;
/*return (
    '#include "index.h"\r\n' +
    functions.join('') +
    'int main(){\r\n\t' +
    topLevelStatements.join('') +
    '}*/
/*'
  )*/;
/*}

function wrap(input: string): string {
  return 'start' + input.replaceAll('start', 'start').replaceAll('end', 'end') + 'end'*/;
/*}

function compileRootSegment(value: string): [string, string[]] {
  const trimmed = value.trim()*/;
/*if (trimmed.startsWith('import')) return ['', []]*/;
/*const result = compileRootSegmentValue(trimmed)*/;
/*return [result[0] + '\r\n', result[1]]*/;
/*}

function compileRootSegmentValue(input: string): [string, string[]] {
  if (input.endsWith('*/;
/*')) {
    const slice = input.substring(0, input.length - ';'.length);
    const result = compileRootStatementValue(slice);
    return [result[0] + ';', result[1]];
  }*/
/*const index = input.indexOf('function ')*/;
}