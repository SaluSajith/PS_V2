package com.hit.pretstreet.pretstreet.storedetails.model;

import java.io.Serializable;

/**
 * Created by User on 01/08/2017.
 */

public class Testimonials implements Serializable {
    String testimonial, name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestimonial() {
        return testimonial;
    }

    public void setTestimonial(String testimonial) {
        this.testimonial = testimonial;
    }
}
