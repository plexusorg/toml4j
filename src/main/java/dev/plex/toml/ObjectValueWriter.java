package dev.plex.toml;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

class ObjectValueWriter implements ValueWriter
{
    static final ValueWriter OBJECT_VALUE_WRITER = new ObjectValueWriter();

    @Override
    public boolean canWrite(Object value)
    {
        return true;
    }

    @Override
    public void write(Object value, WriterContext context)
    {
        Map<String, Object> to = new LinkedHashMap<String, Object>();
        Set<Field> fields = getFields(value.getClass());
        for (Field field : fields)
        {
            if (field.isAnnotationPresent(SerializedName.class))
            {
                to.put(field.getDeclaredAnnotation(SerializedName.class).value(), getFieldValue(field, value));
            } else {
                to.put(field.getName(), getFieldValue(field, value));
            }
        }

        MapValueWriter.MAP_VALUE_WRITER.write(to, context);
    }

    @Override
    public boolean isPrimitiveType()
    {
        return false;
    }

    private static Set<Field> getFields(Class<?> cls)
    {
        Set<Field> fields = new LinkedHashSet<Field>(Arrays.asList(cls.getDeclaredFields()));
        while (cls != Object.class)
        {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }
        removeConstantsAndSyntheticFields(fields);

        return fields;
    }

    private static void removeConstantsAndSyntheticFields(Set<Field> fields)
    {
        Iterator<Field> iterator = fields.iterator();
        while (iterator.hasNext())
        {
            Field field = iterator.next();
            if ((Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) || field.isSynthetic() || Modifier.isTransient(field.getModifiers()))
            {
                iterator.remove();
            }
        }
    }

    private static Object getFieldValue(Field field, Object o)
    {
        boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        Object value = null;
        try
        {
            value = field.get(o);
        } catch (IllegalAccessException ignored)
        {
        }
        field.setAccessible(isAccessible);

        return value;
    }

    private ObjectValueWriter()
    {
    }
}
