/*
 * Copyright 2022 ICON Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foundation.icon.btp.bmc;

import score.ByteArrayObjectWriter;
import score.Context;
import score.ObjectReader;
import score.ObjectWriter;

import java.math.BigInteger;

public class ClaimMessage {
    private BigInteger amount;
    private String receiver;

    public ClaimMessage() {
    }

    public ClaimMessage(BigInteger amount, String receiver) {
        this.amount = amount;
        this.receiver = receiver;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClaimMessage{");
        sb.append("amount=").append(amount);
        sb.append(", receiver='").append(receiver).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static void writeObject(ObjectWriter writer, ClaimMessage obj) {
        obj.writeObject(writer);
    }

    public static ClaimMessage readObject(ObjectReader reader) {
        ClaimMessage obj = new ClaimMessage();
        reader.beginList();
        obj.setAmount(reader.readBigInteger());
        obj.setReceiver(reader.readString());
        reader.end();
        return obj;
    }

    public void writeObject(ObjectWriter writer) {
        writer.beginList(2);
        writer.write(this.getAmount());
        writer.write(this.getReceiver());
        writer.end();
    }

    public static ClaimMessage fromBytes(byte[] bytes) {
        ObjectReader reader = Context.newByteArrayObjectReader("RLPn", bytes);
        return ClaimMessage.readObject(reader);
    }

    public byte[] toBytes() {
        ByteArrayObjectWriter writer = Context.newByteArrayObjectWriter("RLPn");
        ClaimMessage.writeObject(writer, this);
        return writer.toByteArray();
    }
}
