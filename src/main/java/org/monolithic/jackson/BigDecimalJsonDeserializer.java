package org.monolithic.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author xiangqian
 * @date 18:04 2022/05/23
 */
public class BigDecimalJsonDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        if (Objects.isNull(jsonParser.getDecimalValue())) {
            return null;
        }
        return jsonParser.getDecimalValue().setScale(4, RoundingMode.DOWN);
    }

}
