//  The contents of this file are subject to the Mozilla Public License
//  Version 1.1 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License
//  at http://www.mozilla.org/MPL/
//
//  Software distributed under the License is distributed on an "AS IS"
//  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
//  the License for the specific language governing rights and
//  limitations under the License.
//
//  The Original Code is RabbitMQ.
//
//  The Initial Developer of the Original Code is VMware, Inc.
//  Copyright (c) 2007-2011 VMware, Inc.  All rights reserved.
//


package com.rabbitmq.client;

import java.io.IOException;

/**
 * Interface for application callback objects to receive notifications and messages from
 * a queue by subscription.
 * Most consumers will subclass {@link DefaultConsumer}.
 *
 * Note: all methods of this interface are invoked in a dispatch
 * thread which is separate from the {@link Connection}'s thread. This
 * allows {@link Consumer}s to call {@link Channel} or {@link
 * Connection} methods without causing a deadlock.
 * <p/>
 * The dispatch thread is shared by all {@link Consumer}s
 * on the same {@link Channel}.
 * {@link Consumer}s should avoid executing long-running code
 * inside the dispatch thread because this may hold up dispatch of
 * messages to other {@link Consumer}s.
 *
 * @see Channel#basicConsume
 * @see Channel#basicCancel
 */
public interface Consumer {
    /**
     * Called when the consumer is first registered by a call to {@link Channel#basicConsume}.
     *
     * Note: if the consumer was registered with an empty string for its consumerTag
     * this will be the server-generated consumerTag.
     * @param consumerTag the defined consumerTag (either client- or server-generated)
     */
    void handleConsumeOk(String consumerTag);

    /**
     * Called when the consumer is deregistered by a call to {@link Channel#basicCancel}.
     * @param consumerTag the defined consumerTag (either client- or server-generated)
     */
    void handleCancelOk(String consumerTag);

    /**
     * Called when the consumer is cancelled for reasons other than by a
     * basicCancel: e.g. the queue has been deleted (either by this channel or
     * by any other channel). See handleCancelOk for notification of consumer
     * cancellation due to basicCancel.
     *
     * @throws IOException
     */
    void handleCancel(String consumerTag) throws IOException;

    /**
     * Called to the consumer that either the channel or the underlying connection has been shut down.
     * @param consumerTag the defined consumerTag (either client- or server-generated)
     * @param sig an exception object encapsulating the reason for shutdown
     */
    void handleShutdownSignal(String consumerTag, ShutdownSignalException sig);

    /**
     * Called to notify the consumer that we've received a <code>basic.recover-ok</code>
     * in reply to a <code>basic.recover</code> some other thread sent. All messages
     * received before this is invoked that haven't been ack'ed will be
     * re-delivered. All messages received afterwards won't be.
     * <p/><b>Note</b><br/>
     * If {@link Consumer} callbacks are invoked by the
     * connection main loop thread - it's sometimes useful to allow that
     * thread to know that the recover-ok has been received, rather than the
     * thread which invoked basicRecover().
     * <p/>
     * It is no longer the case that callbacks are invoked on the connection
     * main loop thread. The justification for this callback is therefore less
     * sound.  However, it is retained for compatibility.
     */
    void handleRecoverOk();

    /**
     * Called when a delivery appears for this consumer.
     * @param consumerTag the defined consumerTag (either client- or server-generated)
     * @param envelope packaging data for the message
     * @param properties content header data for the message
     * @param body the message body (opaque client-specific byte array)
     * @throws IOException if the consumer hits an I/O error while processing the message
     */
    void handleDelivery(String consumerTag,
                        Envelope envelope,
                        AMQP.BasicProperties properties,
                        byte[] body)
        throws IOException;
}
