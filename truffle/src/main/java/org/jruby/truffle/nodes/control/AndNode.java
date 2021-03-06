/*
 * Copyright (c) 2013, 2015 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.nodes.control;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.utilities.ConditionProfile;
import org.jruby.truffle.nodes.RubyNode;
import org.jruby.truffle.nodes.cast.BooleanCastNode;
import org.jruby.truffle.nodes.cast.BooleanCastNodeFactory;
import org.jruby.truffle.runtime.RubyContext;

/**
 * Represents a Ruby {@code and} or {@code &&} expression.
 */
public class AndNode extends RubyNode {

    @Child private RubyNode left;
    @Child private BooleanCastNode leftCast;
    @Child private RubyNode right;
    private final ConditionProfile conditionProfile = ConditionProfile.createCountingProfile();

    public AndNode(RubyContext context, SourceSection sourceSection, RubyNode left, RubyNode right) {
        super(context, sourceSection);
        this.left = left;
        leftCast = BooleanCastNodeFactory.create(context, sourceSection, null);
        this.right = right;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        final Object leftValue = left.execute(frame);
        if (conditionProfile.profile(leftCast.executeBoolean(frame, leftValue))) {
            // Right expression evaluated and returned if left expression returns true.
            return right.execute(frame);
        } else {
            return leftValue;
        }
    }
}
