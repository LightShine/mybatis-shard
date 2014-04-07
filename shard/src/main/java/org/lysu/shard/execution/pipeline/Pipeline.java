package org.lysu.shard.execution.pipeline;

import java.util.List;
import java.util.Map;

/**
 * @author lysu created on 14-4-7 下午11:08
 * @version $Id$
 */
public interface Pipeline {

    void addFirst(String name, PipeHandler handler);

    void addLast(String name, PipeHandler handler);

    void addBefore(String baseName, String name, PipeHandler handler);

    void addAfter(String baseName, String name, PipeHandler handler);

    void remove(PipeHandler handler);

    PipeHandler remove(String name);

    <T extends PipeHandler> T remove(Class<T> handlerType);

    PipeHandler removeFirst();

    PipeHandler removeLast();

    void replace(PipeHandler oldHandler, String newName, PipeHandler newHandler);

    PipeHandler replace(String oldName, String newName, PipeHandler newHandler);

    <T extends PipeHandler> T replace(Class<T> oldHandlerType, String newName, PipeHandler newHandler);

    PipeHandler getFirst();

    PipeHandler getLast();

    PipeHandler get(String name);

    <T extends PipeHandler> T get(Class<T> handlerType);

    PipeContext getContext(PipeHandler handler);

    PipeContext getContext(String name);

    PipeContext getContext(Class<? extends PipeHandler> handlerType);

    List<String> getNames();

    Map<String, PipeHandler> toMap();

    void doUpstream(PipelineEvent event);


}
