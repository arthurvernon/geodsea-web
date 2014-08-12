package com.geodsea.pub.web.rest.dto;


/**
 * Data from google maps autocomplete service.
 * <p>
 * See <a href="https://developers.google.com/maps/documentation/javascript/reference?hl=nl#Autocomplete">
 * Google Javascript documentation for Autocomplete</a>.
 * </p>
 * <p>This class represents one element in the array of address parts. For
 * 32 Brighton Mews, Hillarys, Western Australia, Australia then the following components would be generated</p>
 * <p/>
 *<pre><code>[
 *  {"long_name":"32","short_name":"32","types":["street_number"]},
 *  {"long_name":"Brighton Mews","short_name":"Brighton Mews","types":["route"]},
 *  {"long_name":"Hillarys","short_name":"Hillarys","types":["locality","political"]},
 *  {"long_name":"Western Australia","short_name":"WA","types":["administrative_area_level_1","political"]},
 *  {"long_name":"Australia","short_name":"AU","types":["country","political"]},
 *  {"long_name":"6025","short_name":"6025","types":["postal_code"]}
 *  ]</code>
 * </pre>
 */
public class AddressPartDTO {

    private String long_name;

    private String short_name;

    private String[] types;

    public AddressPartDTO(String long_name, String short_name, String[] types) {
        this.long_name = long_name;
        this.short_name = short_name;
        this.types = types;
    }

    public AddressPartDTO() {
    }

    public String getLong_name() {
        return long_name;
    }

    public void setLong_name(String long_name) {
        this.long_name = long_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return long_name;
    }
}
