package org.seasar.kvasir.cms.ymir.impl;

import static net.skirnir.freyja.zpt.tales.NotePathResolver.NAMEPREFIX_SIZE;
import static net.skirnir.freyja.zpt.tales.NotePathResolver.NAMESUFFIX_SIZE;
import static net.skirnir.freyja.zpt.tales.NotePathResolver.NAME_CATEGORIES;
import static net.skirnir.freyja.zpt.tales.NotePathResolver.NAME_SIZE;
import static net.skirnir.freyja.zpt.tales.NotePathResolver.NAME_VALUE;

import org.seasar.cms.ymir.Note;
import org.seasar.cms.ymir.Notes;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.zpt.tales.NoteLocalizer;
import net.skirnir.freyja.zpt.tales.PathResolver;


// TODO このクラスはYmir-1.0系を利用するまでの一時的なものである。Ymir-1.0系を利用するようになった時は
// Ymirが持つYmirPathResolverを使うようにすること。
public class YmirPathResolver
    implements PathResolver
{
    private NoteLocalizer noteLocalizer_;


    public NoteLocalizer getNoteLocalizer()
    {
        return noteLocalizer_;
    }


    public YmirPathResolver setNoteLocalizer(NoteLocalizer noteLocalizer)
    {
        noteLocalizer_ = noteLocalizer;
        return this;
    }


    public boolean accept(TemplateContext context,
        VariableResolver varResolver, Object obj, String child)
    {
        return (obj instanceof Notes || obj instanceof Note);
    }


    public Object resolve(TemplateContext context,
        VariableResolver varResolver, Object obj, String child)
    {
        if (obj instanceof Notes) {
            Notes notes = (Notes)obj;
            if (child.equals(NAME_SIZE)) {
                return notes.size();
            } else if (child.startsWith(NAMEPREFIX_SIZE)
                && child.endsWith(NAMESUFFIX_SIZE)) {
                return notes.size(child.substring(NAMEPREFIX_SIZE.length(),
                    child.length() - NAMESUFFIX_SIZE.length()));
            } else if (child.equals(NAME_CATEGORIES)) {
                return notes.categories();
            } else if (notes.size(child) > 0) {
                return notes.get(child);
            }
        } else if (obj instanceof Note) {
            Note note = (Note)obj;
            if (child.equals(NAME_VALUE) && noteLocalizer_ != null) {
                return noteLocalizer_.getMessageResourceValue(context,
                    varResolver, note.getValue(), note.getParameters());
            }
        }
        return null;
    }
}
