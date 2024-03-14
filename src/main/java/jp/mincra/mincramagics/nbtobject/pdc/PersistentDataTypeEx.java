package jp.mincra.mincramagics.nbtobject.pdc;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public interface PersistentDataTypeEx {
    PersistentDataType<String[], String[]> STRING_ARRAY = new PrimitivePersistentDataType<>(String[].class);

    class PrimitivePersistentDataType<T> implements PersistentDataType<T, T> {

        private final Class<T> primitiveType;

        PrimitivePersistentDataType(@NotNull Class<T> primitiveType) {
            this.primitiveType = primitiveType;
        }

        @NotNull
        @Override
        public Class<T> getPrimitiveType() {
            return primitiveType;
        }

        @NotNull
        @Override
        public Class<T> getComplexType() {
            return primitiveType;
        }

        @NotNull
        @Override
        public T toPrimitive(@NotNull T complex, @NotNull PersistentDataAdapterContext context) {
            return complex;
        }

        @NotNull
        @Override
        public T fromPrimitive(@NotNull T primitive, @NotNull PersistentDataAdapterContext context) {
            return primitive;
        }
    }
}
