package dev.plex.toml;

import java.util.concurrent.atomic.AtomicInteger;

class MultilineStringValueReader implements ValueReader
{

  static final MultilineStringValueReader MULTILINE_STRING_VALUE_READER = new MultilineStringValueReader();

  @Override
  public boolean canRead(String s) {
    return s.startsWith("\"\"\"");
  }

  @Override
  public Object read(String s, AtomicInteger index, dev.plex.toml.Context context) {
    AtomicInteger line = context.line;
    int startLine = line.get();
    int originalStartIndex = index.get();
    int startIndex = index.addAndGet(3);
    int endIndex = -1;
    
    if (s.charAt(startIndex) == '\n') {
      startIndex = index.incrementAndGet();
      line.incrementAndGet();
    }
    
    for (int i = startIndex; i < s.length(); i = index.incrementAndGet()) {
      char c = s.charAt(i);
      
      if (c == '\n') {
        line.incrementAndGet();
      } else if (c == '"' && s.length() > i + 2 && s.charAt(i + 1) == '"' && s.charAt(i + 2) == '"') {
        endIndex = i;
        index.addAndGet(2);
        break;
      }
    }
    
    if (endIndex == -1) {
      dev.plex.toml.Results.Errors errors = new dev.plex.toml.Results.Errors();
      errors.unterminated(context.identifier.getName(), s.substring(originalStartIndex), startLine);
      return errors;
    }

    s = s.substring(startIndex, endIndex);
    s = s.replaceAll("\\\\\\s+", "");
    s = dev.plex.toml.StringValueReaderWriter.STRING_VALUE_READER_WRITER.replaceUnicodeCharacters(s);
    s = dev.plex.toml.StringValueReaderWriter.STRING_VALUE_READER_WRITER.replaceSpecialCharacters(s);

    return s;
  }

  private MultilineStringValueReader() {
  }

}
