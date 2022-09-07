package org.monolithic.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author xiangqian
 * @date 18:02 2022/05/23
 */
public class BigDecimalJsonSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (Objects.isNull(bigDecimal)) {
            jsonGenerator.writeNull();
            return;
        }
        jsonGenerator.writeNumber(bigDecimal.setScale(4, RoundingMode.DOWN));
    }

}
