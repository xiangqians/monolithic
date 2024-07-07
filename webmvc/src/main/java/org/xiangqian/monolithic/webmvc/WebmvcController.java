package org.xiangqian.monolithic.webmvc;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.xiangqian.monolithic.common.model.Code;
import org.xiangqian.monolithic.common.model.Result;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author xiangqian
 * @date 20:57 2023/10/16
 */
public abstract class WebmvcController {

    protected <T> Result<T> result(T t) {
        return new Result<>(Code.OK, t);
    }

    protected ResponseEntity<Resource> resource(Resource resource) throws IOException {
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8))
                .body(resource);
    }

}
