package org.lysu.shard.execution.pipeline;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author lysu created on 14-4-7 下午11:23
 * @version $Id$
 */
public class DefaultPipeline implements Pipeline {

    private final Map<String, DefaultPipeContext> name2ctx = Maps.newHashMapWithExpectedSize(4);
    private volatile DefaultPipeContext head;
    private volatile DefaultPipeContext tail;

    public DefaultPipeline() {
    }

    @Override
    public synchronized void addFirst(String name, PipeHandler handler) {
        if (name2ctx.isEmpty()) {
            init(name, handler);
        } else {
            checkDuplicateName(name);
            DefaultPipeContext oldHead = head;
            DefaultPipeContext newHead = new DefaultPipeContext(null, oldHead, name, handler);

            oldHead.prev = newHead;
            head = newHead;
            name2ctx.put(name, newHead);
        }
    }

    public synchronized void addLast(String name, PipeHandler handler) {
        if (name2ctx.isEmpty()) {
            init(name, handler);
        } else {
            checkDuplicateName(name);
            DefaultPipeContext oldTail = tail;
            DefaultPipeContext newTail = new DefaultPipeContext(oldTail, null, name, handler);

            oldTail.next = newTail;
            tail = newTail;
            name2ctx.put(name, newTail);

        }
    }

    public synchronized void addBefore(String baseName, String name, PipeHandler handler) {
        DefaultPipeContext ctx = getContextOrDie(baseName);
        if (ctx == head) {
            addFirst(name, handler);
        } else {
            checkDuplicateName(name);
            DefaultPipeContext newCtx = new DefaultPipeContext(ctx.prev, ctx, name, handler);

            ctx.prev.next = newCtx;
            ctx.prev = newCtx;
            name2ctx.put(name, newCtx);

        }
    }

    public synchronized void addAfter(String baseName, String name, PipeHandler handler) {
        DefaultPipeContext ctx = getContextOrDie(baseName);
        if (ctx == tail) {
            addLast(name, handler);
        } else {
            checkDuplicateName(name);
            DefaultPipeContext newCtx = new DefaultPipeContext(ctx, ctx.next, name, handler);

            ctx.next.prev = newCtx;
            ctx.next = newCtx;
            name2ctx.put(name, newCtx);

        }
    }

    public synchronized void remove(PipeHandler handler) {
        remove(getContextOrDie(handler));
    }

    public synchronized PipeHandler remove(String name) {
        return remove(getContextOrDie(name)).getHandler();
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends PipeHandler> T remove(Class<T> handlerType) {
        return (T) remove(getContextOrDie(handlerType)).getHandler();
    }

    private DefaultPipeContext remove(DefaultPipeContext ctx) {
        if (head == tail) {
            head = tail = null;
            name2ctx.clear();
        } else if (ctx == head) {
            removeFirst();
        } else if (ctx == tail) {
            removeLast();
        } else {
            DefaultPipeContext prev = ctx.prev;
            DefaultPipeContext next = ctx.next;
            prev.next = next;
            next.prev = prev;
            name2ctx.remove(ctx.getName());
        }
        return ctx;
    }

    public synchronized PipeHandler removeFirst() {
        if (name2ctx.isEmpty()) {
            throw new NoSuchElementException();
        }

        DefaultPipeContext oldHead = head;
        if (oldHead == null) {
            throw new NoSuchElementException();
        }

        if (oldHead.next == null) {
            head = tail = null;
            name2ctx.clear();
        } else {
            oldHead.next.prev = null;
            head = oldHead.next;
            name2ctx.remove(oldHead.getName());
        }

        return oldHead.getHandler();
    }

    public synchronized PipeHandler removeLast() {
        if (name2ctx.isEmpty()) {
            throw new NoSuchElementException();
        }

        DefaultPipeContext oldTail = tail;
        if (oldTail == null) {
            throw new NoSuchElementException();
        }

        if (oldTail.prev == null) {
            head = tail = null;
            name2ctx.clear();
        } else {
            oldTail.prev.next = null;
            tail = oldTail.prev;
            name2ctx.remove(oldTail.getName());
        }

        return oldTail.getHandler();
    }

    public synchronized void replace(PipeHandler oldHandler, String newName, PipeHandler newHandler) {
        replace(getContextOrDie(oldHandler), newName, newHandler);
    }

    public synchronized PipeHandler replace(String oldName, String newName, PipeHandler newHandler) {
        return replace(getContextOrDie(oldName), newName, newHandler);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends PipeHandler> T replace(Class<T> oldHandlerType, String newName,
            PipeHandler newHandler) {
        return (T) replace(getContextOrDie(oldHandlerType), newName, newHandler);
    }

    private PipeHandler replace(DefaultPipeContext ctx, String newName, PipeHandler newHandler) {
        if (ctx == head) {
            removeFirst();
            addFirst(newName, newHandler);
        } else if (ctx == tail) {
            removeLast();
            addLast(newName, newHandler);
        } else {
            boolean sameName = ctx.getName().equals(newName);
            if (!sameName) {
                checkDuplicateName(newName);
            }

            DefaultPipeContext prev = ctx.prev;
            DefaultPipeContext next = ctx.next;
            DefaultPipeContext newCtx = new DefaultPipeContext(prev, next, newName, newHandler);

            prev.next = newCtx;
            next.prev = newCtx;

            if (!sameName) {
                name2ctx.remove(ctx.getName());
            }
            name2ctx.put(newName, newCtx);

        }

        return ctx.getHandler();
    }

    public synchronized PipeHandler getFirst() {
        DefaultPipeContext head = this.head;
        if (head == null) {
            return null;
        }
        return head.getHandler();
    }

    public synchronized PipeHandler getLast() {
        DefaultPipeContext tail = this.tail;
        if (tail == null) {
            return null;
        }
        return tail.getHandler();
    }

    public synchronized PipeHandler get(String name) {
        DefaultPipeContext ctx = name2ctx.get(name);
        if (ctx == null) {
            return null;
        } else {
            return ctx.getHandler();
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends PipeHandler> T get(Class<T> handlerType) {
        DefaultPipeContext ctx = getContext(handlerType);
        if (ctx == null) {
            return null;
        } else {
            return (T) ctx.getHandler();
        }
    }

    public synchronized PipeContext getContext(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return name2ctx.get(name);
    }

    public synchronized PipeContext getContext(PipeHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        if (name2ctx.isEmpty()) {
            return null;
        }
        DefaultPipeContext ctx = head;
        for (;;) {
            if (ctx.getHandler() == handler) {
                return ctx;
            }

            ctx = ctx.next;
            if (ctx == null) {
                break;
            }
        }
        return null;
    }

    public synchronized DefaultPipeContext getContext(Class<? extends PipeHandler> handlerType) {
        if (handlerType == null) {
            throw new NullPointerException("handlerType");
        }

        if (name2ctx.isEmpty()) {
            return null;
        }
        DefaultPipeContext ctx = head;
        for (;;) {
            if (handlerType.isAssignableFrom(ctx.getHandler().getClass())) {
                return ctx;
            }

            ctx = ctx.next;
            if (ctx == null) {
                break;
            }
        }
        return null;
    }

    public List<String> getNames() {
        List<String> list = Lists.newArrayList();
        if (name2ctx.isEmpty()) {
            return list;
        }

        DefaultPipeContext ctx = head;
        for (;;) {
            list.add(ctx.getName());
            ctx = ctx.next;
            if (ctx == null) {
                break;
            }
        }
        return list;
    }

    public Map<String, PipeHandler> toMap() {
        Map<String, PipeHandler> map = Maps.newLinkedHashMap();
        if (name2ctx.isEmpty()) {
            return map;
        }

        DefaultPipeContext ctx = head;
        for (;;) {
            map.put(ctx.getName(), ctx.getHandler());
            ctx = ctx.next;
            if (ctx == null) {
                break;
            }
        }
        return map;
    }

    @Override
    public void doUpstream(PipelineEvent event) {
        DefaultPipeContext head = this.head;
        if (head == null) {
            return;
        }
        doUpstream(head, event);
    }

    void doUpstream(DefaultPipeContext ctx, PipelineEvent e) {
        ctx.getHandler().handle(ctx, e);
    }

    private void checkDuplicateName(String name) {
        Preconditions.checkArgument(name2ctx.containsKey(name), "Duplicate handler name: " + name);
    }

    private DefaultPipeContext getContextOrDie(String name) {
        DefaultPipeContext ctx = (DefaultPipeContext) getContext(name);
        if (ctx == null) {
            throw new NoSuchElementException(name);
        } else {
            return ctx;
        }
    }

    private DefaultPipeContext getContextOrDie(PipeHandler handler) {
        DefaultPipeContext ctx = (DefaultPipeContext) getContext(handler);
        if (ctx == null) {
            throw new NoSuchElementException(handler.getClass().getName());
        } else {
            return ctx;
        }
    }

    private DefaultPipeContext getContextOrDie(Class<? extends PipeHandler> handlerType) {
        DefaultPipeContext ctx = getContext(handlerType);
        if (ctx == null) {
            throw new NoSuchElementException(handlerType.getName());
        } else {
            return ctx;
        }
    }

    private void init(String name, PipeHandler handler) {
        DefaultPipeContext ctx = new DefaultPipeContext(null, null, name, handler);
        head = tail = ctx;
        name2ctx.clear();
        name2ctx.put(name, ctx);
    }

    private final class DefaultPipeContext implements PipeContext {

        private final String name;
        private final PipeHandler handler;
        volatile DefaultPipeContext next;
        volatile DefaultPipeContext prev;

        DefaultPipeContext(DefaultPipeContext prev, DefaultPipeContext next, String name, PipeHandler handler) {

            if (name == null) {
                throw new NullPointerException("name");
            }
            if (handler == null) {
                throw new NullPointerException("handler");
            }

            this.prev = prev;
            this.next = next;
            this.name = name;
            this.handler = handler;
        }

        public String getName() {
            return name;
        }

        public PipeHandler getHandler() {
            return handler;
        }

        public void doUpstream(PipelineEvent event) {
            DefaultPipeContext next = getActualFlowContext(this);
            if (next == null) {
                return;
            }
            DefaultPipeline.this.doUpstream(next, event);
        }

        DefaultPipeContext getActualFlowContext(DefaultPipeContext ctx) {
            if (ctx == null) {
                return null;
            }

            DefaultPipeContext next = ctx.next;
            if (next == null) {
                return null;
            }

            return next;
        }

    }

}
