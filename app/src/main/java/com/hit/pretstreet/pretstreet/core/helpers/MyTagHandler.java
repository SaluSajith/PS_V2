package com.hit.pretstreet.pretstreet.core.helpers;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

/**
 * Created by hit on 3/11/15.
 */
public class MyTagHandler implements Html.TagHandler {

    boolean first = true;
    String parent = null;
    int index = 1;

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

        if (tag.equals("ul")) parent = "ul";
        else if (tag.equals("ol")) parent = "ol";
        if (tag.equals("li")) {
            if (parent.equals("ul")) {
                if (first) {
                    output.append("\n\t• ");
                    first = false;
                } else {
                    first = true;
                }
            } else {
                if (first) {
                    output.append("\n\t" + index + ". ");
                    first = false;
                    index++;
                } else {
                    first = true;
                }
            }
        }
        /*if (tag.equals("li")) {
            char lastChar = 0;
            if (output.length() > 0)
                lastChar = output.charAt(output.length() - 1);
            if (first) {
                if (lastChar == '\n')
                    output.append("\t•  ");
                else
                    output.append("\n\t•  ");
                first = false;
            } else {
                first = true;
            }
        }*/
    }
}
