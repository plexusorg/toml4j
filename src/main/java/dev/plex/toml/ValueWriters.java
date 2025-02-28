package dev.plex.toml;

class ValueWriters {

  static final ValueWriters WRITERS = new ValueWriters();

  ValueWriter findWriterFor(Object value) {
    for (ValueWriter valueWriter : VALUE_WRITERS) {
      if (valueWriter.canWrite(value)) {
        return valueWriter;
      }
    }

    return ObjectValueWriter.OBJECT_VALUE_WRITER;
  }

  private ValueWriters() {}
  
  private static dev.plex.toml.DateValueReaderWriter getPlatformSpecificDateConverter() {
    String specificationVersion = Runtime.class.getPackage().getSpecificationVersion();
    return specificationVersion != null && specificationVersion.startsWith("1.6") ? dev.plex.toml.DateValueReaderWriter.DATE_PARSER_JDK_6 : dev.plex.toml.DateValueReaderWriter.DATE_VALUE_READER_WRITER;
  }

  private static final ValueWriter[] VALUE_WRITERS = {
      StringValueReaderWriter.STRING_VALUE_READER_WRITER, NumberValueReaderWriter.NUMBER_VALUE_READER_WRITER, dev.plex.toml.BooleanValueReaderWriter.BOOLEAN_VALUE_READER_WRITER, getPlatformSpecificDateConverter(),
      MapValueWriter.MAP_VALUE_WRITER, dev.plex.toml.PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER, TableArrayValueWriter.TABLE_ARRAY_VALUE_WRITER
  };
}
