package org.seasar.kvasir.page.gard.delta.impl;

import org.seasar.kvasir.page.gard.delta.AddDelta;
import org.seasar.kvasir.page.gard.delta.AttributeDifference;
import org.seasar.kvasir.page.gard.delta.ChangeDelta;
import org.seasar.kvasir.page.gard.delta.Delta;
import org.seasar.kvasir.page.gard.delta.RemoveDelta;


/**
 * 
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class AttributeDifferenceImpl
    implements AttributeDifference
{
    private Entry   stringEntry_ = new Entry();
    private Entry   streamEntry_ = new Entry();


    public AttributeDifferenceImpl()
    {
    }

/*
    public AttributeDifferenceImpl(Attribute toAttr, Attribute fromAttr)
    {
        String[] variants = (toAttr != null)
            ? toAttr.getVariants() : new String[0];
        for (int i = 0; i < variants.length; i++) {
            String variant = variants[i];

            Attribute.Entry toEntry = toAttr.getEntry(variant);
            Attribute.Entry fromEntry = (fromAttr != null)
                ? fromAttr.getEntry(variant) : null;
            Delta delta = getDelta(name, )
                
        }
        for (Iterator itr = toAttr.stringNames(); itr.hasNext(); ) {
            String name = (String)itr.next();

            String fromStr = fromAttr.getString(name);
            String toStr = toAttr.getString(name);
            Delta delta = getDelta(name, toStr, fromStr);
            if (delta != null) {
                addStringDelta(delta);
            }
        }

        for (Iterator itr = fromAttr.stringNames(); itr.hasNext(); ) {
            String name = (String)itr.next();

            if (!toAttr.stringExists(name)) {
                String fromStr = fromAttr.getString(name);
                addStringDelta(new RemoveDelta(name, fromStr));
            }
        }

        for (Iterator itr = toAttr.streamNames(); itr.hasNext(); ) {
            String name = (String)itr.next();

            InputStreamFactory fromIsf = fromAttr.getStream(name);
            InputStreamFactory toIsf = toAttr.getStream(name);
            Delta delta = getDelta(name, toIsf, fromIsf);
            if (delta != null) {
                addStreamDelta(delta);
            }
        }

        for (Iterator itr = fromAttr.streamNames(); itr.hasNext(); ) {
            String name = (String)itr.next();

            if (!toAttr.streamExists(name)) {
                InputStreamFactory fromIsf = fromAttr.getStream(name);
                addStreamDelta(new RemoveDelta(name, fromIsf));
            }
        }
    }
    */


    public void addStringDelta(Delta delta)
    {
        if (delta == null) {
            return;
        }
        stringEntry_.addDelta(delta);
    }


    public Delta[] getStringDeltas(int type)
    {
        return stringEntry_.getDeltas(type);
    }


    public void addStreamDelta(Delta delta)
    {
        if (delta == null) {
            return;
        }
        streamEntry_.addDelta(delta);
    }


    public Delta[] getStreamDeltas(int type)
    {
        return streamEntry_.getDeltas(type);
    }


    /*
     * private scope methods
     */

    private Delta getDelta(String name, Object to, Object from)
    {
        if (to == null) {
            if (from == null) {
                return null;
            } else {
                return new RemoveDelta(name, from);
            }
        } else {
            if (from == null) {
                return new AddDelta(name, to);
            } else {
                if (to.equals(from)) {
                    return null;
                } else {
                    return new ChangeDelta(name, to, from);
                }
            }
        }
    }
}
