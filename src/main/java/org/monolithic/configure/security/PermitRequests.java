package org.monolithic.configure.security;

import java.util.Collection;
import java.util.List;

/**
 * @author xiangqian
 * @date 21:50 2022/09/07
 */
public class PermitRequests {

    private Collection<PermitRequest> permitRequests;

    public PermitRequests(Collection<PermitRequest> permitRequests) {
        this.permitRequests = permitRequests;
    }

    public PermitRequests(PermitRequest... permitRequests) {
        this.permitRequests = List.of(permitRequests);
    }

    public Collection<PermitRequest> get() {
        return permitRequests;
    }

}
