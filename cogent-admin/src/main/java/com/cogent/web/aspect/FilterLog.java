package com.cogent.web.aspect;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.cogent.common.utils.StringUtils;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/5
 * {@code @description:}
 */
public class FilterLog  extends Filter<ILoggingEvent>{


    @Override
    public FilterReply decide(ILoggingEvent event) {

        if (StringUtils.containsIgnoreCase(event.getFormattedMessage(),"statusReport")) {
            return FilterReply.DENY;
        } else {
            return FilterReply.NEUTRAL;
        }
    }
}
