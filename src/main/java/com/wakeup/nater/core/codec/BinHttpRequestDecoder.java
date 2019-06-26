//package com.wakeup.nater.core.codec;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.http.*;
//import io.netty.util.internal.AppendableCharSequence;
//
//import java.util.List;
//
///**
// * @Description
// * @Author Alon
// * @Date 2019/6/22 22:12
// */
//public class BinHttpRequestDecoder extends HttpObjectDecoder {
//
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
//        if (resetRequested) {
//            resetNow();
//        }
//
//        switch (currentState) {
//            case SKIP_CONTROL_CHARS: {
//                if (!skipControlCharacters(buffer)) {
//                    return;
//                }
//                currentState = State.READ_INITIAL;
//            }
//            case READ_INITIAL: try {
//                AppendableCharSequence line = lineParser.parse(buffer);
//                if (line == null) {
//                    return;
//                }
//                String[] initialLine = splitInitialLine(line);
//                if (initialLine.length < 3) {
//                    // Invalid initial line - ignore.
//                    currentState = State.SKIP_CONTROL_CHARS;
//                    return;
//                }
//
//                message = createMessage(initialLine);
//                currentState = State.READ_HEADER;
//                // fall-through
//            } catch (Exception e) {
//                out.add(invalidMessage(buffer, e));
//                return;
//            }
//            case READ_HEADER: try {
//                State nextState = readHeaders(buffer);
//                if (nextState == null) {
//                    return;
//                }
//                currentState = nextState;
//                switch (nextState) {
//                    case SKIP_CONTROL_CHARS:
//                        // fast-path
//                        // No content is expected.
//                        out.add(message);
//                        out.add(LastHttpContent.EMPTY_LAST_CONTENT);
//                        resetNow();
//                        return;
//                    case READ_CHUNK_SIZE:
//                        if (!chunkedSupported) {
//                            throw new IllegalArgumentException("Chunked messages not supported");
//                        }
//                        // Chunked encoding - generate HttpMessage first.  HttpChunks will follow.
//                        out.add(message);
//                        return;
//                    default:
//                        /**
//                         * <a href="https://tools.ietf.org/html/rfc7230#section-3.3.3">RFC 7230, 3.3.3</a> states that if a
//                         * request does not have either a transfer-encoding or a content-length header then the message body
//                         * length is 0. However for a response the body length is the number of octets received prior to the
//                         * server closing the connection. So we treat this as variable length chunked encoding.
//                         */
//                        long contentLength = contentLength();
//                        if (contentLength == 0 || contentLength == -1 && isDecodingRequest()) {
//                            out.add(message);
//                            out.add(LastHttpContent.EMPTY_LAST_CONTENT);
//                            resetNow();
//                            return;
//                        }
//
//                        assert nextState == State.READ_FIXED_LENGTH_CONTENT ||
//                                nextState == State.READ_VARIABLE_LENGTH_CONTENT;
//
//                        out.add(message);
//
//                        if (nextState == State.READ_FIXED_LENGTH_CONTENT) {
//                            // chunkSize will be decreased as the READ_FIXED_LENGTH_CONTENT state reads data chunk by chunk.
//                            chunkSize = contentLength;
//                        }
//
//                        // We return here, this forces decode to be called again where we will decode the content
//                        return;
//                }
//            } catch (Exception e) {
//                out.add(invalidMessage(buffer, e));
//                return;
//            }
//            case READ_VARIABLE_LENGTH_CONTENT: {
//                // Keep reading data as a chunk until the end of connection is reached.
//                int toRead = Math.min(buffer.readableBytes(), maxChunkSize);
//                if (toRead > 0) {
//                    ByteBuf content = buffer.readRetainedSlice(toRead);
//                    out.add(new DefaultHttpContent(content));
//                }
//                return;
//            }
//            case READ_FIXED_LENGTH_CONTENT: {
//                int readLimit = buffer.readableBytes();
//
//                // Check if the buffer is readable first as we use the readable byte count
//                // to create the HttpChunk. This is needed as otherwise we may end up with
//                // create a HttpChunk instance that contains an empty buffer and so is
//                // handled like it is the last HttpChunk.
//                //
//                // See https://github.com/netty/netty/issues/433
//                if (readLimit == 0) {
//                    return;
//                }
//
//                int toRead = Math.min(readLimit, maxChunkSize);
//                if (toRead > chunkSize) {
//                    toRead = (int) chunkSize;
//                }
//                ByteBuf content = buffer.readRetainedSlice(toRead);
//                chunkSize -= toRead;
//
//                if (chunkSize == 0) {
//                    // Read all content.
//                    out.add(new DefaultLastHttpContent(content, validateHeaders));
//                    resetNow();
//                } else {
//                    out.add(new DefaultHttpContent(content));
//                }
//                return;
//            }
//            /**
//             * everything else after this point takes care of reading chunked content. basically, read chunk size,
//             * read chunk, read and ignore the CRLF and repeat until 0
//             */
//            case READ_CHUNK_SIZE: try {
//                AppendableCharSequence line = lineParser.parse(buffer);
//                if (line == null) {
//                    return;
//                }
//                int chunkSize = getChunkSize(line.toString());
//                this.chunkSize = chunkSize;
//                if (chunkSize == 0) {
//                    currentState = State.READ_CHUNK_FOOTER;
//                    return;
//                }
//                currentState = State.READ_CHUNKED_CONTENT;
//                // fall-through
//            } catch (Exception e) {
//                out.add(invalidChunk(buffer, e));
//                return;
//            }
//            case READ_CHUNKED_CONTENT: {
//                assert chunkSize <= Integer.MAX_VALUE;
//                int toRead = Math.min((int) chunkSize, maxChunkSize);
//                toRead = Math.min(toRead, buffer.readableBytes());
//                if (toRead == 0) {
//                    return;
//                }
//                HttpContent chunk = new DefaultHttpContent(buffer.readRetainedSlice(toRead));
//                chunkSize -= toRead;
//
//                out.add(chunk);
//
//                if (chunkSize != 0) {
//                    return;
//                }
//                currentState = State.READ_CHUNK_DELIMITER;
//                // fall-through
//            }
//            case READ_CHUNK_DELIMITER: {
//                final int wIdx = buffer.writerIndex();
//                int rIdx = buffer.readerIndex();
//                while (wIdx > rIdx) {
//                    byte next = buffer.getByte(rIdx++);
//                    if (next == HttpConstants.LF) {
//                        currentState = State.READ_CHUNK_SIZE;
//                        break;
//                    }
//                }
//                buffer.readerIndex(rIdx);
//                return;
//            }
//            case READ_CHUNK_FOOTER: try {
//                LastHttpContent trailer = readTrailingHeaders(buffer);
//                if (trailer == null) {
//                    return;
//                }
//                out.add(trailer);
//                resetNow();
//                return;
//            } catch (Exception e) {
//                out.add(invalidChunk(buffer, e));
//                return;
//            }
//            case BAD_MESSAGE: {
//                // Keep discarding until disconnection.
//                buffer.skipBytes(buffer.readableBytes());
//                break;
//            }
//            case UPGRADED: {
//                int readableBytes = buffer.readableBytes();
//                if (readableBytes > 0) {
//                    // Keep on consuming as otherwise we may trigger an DecoderException,
//                    // other handler will replace this codec with the upgraded protocol codec to
//                    // take the traffic over at some point then.
//                    // See https://github.com/netty/netty/issues/2173
//                    out.add(buffer.readBytes(readableBytes));
//                }
//                break;
//            }
//        }
//    }
//
//    @Override
//    protected HttpMessage createMessage(String[] initialLine) throws Exception {
//        return new DefaultHttpRequest(
//                HttpVersion.valueOf(initialLine[2]),
//                HttpMethod.valueOf(initialLine[0]), initialLine[1], validateHeaders);
//    }
//
//    @Override
//    protected HttpMessage createInvalidMessage() {
//        return new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/bad-request", validateHeaders);
//    }
//
//    @Override
//    protected boolean isDecodingRequest() {
//        return true;
//    }
//}
