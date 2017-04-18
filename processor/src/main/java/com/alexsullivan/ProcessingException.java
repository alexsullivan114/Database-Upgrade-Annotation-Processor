package com.alexsullivan;

import javax.lang.model.element.Element;

class ProcessingException extends Exception {

    private Element element;

    ProcessingException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}