package com.fasterxml.jackson.core;

import java.io.InputStream;
import java.io.Reader;

/**
 * Token reader (parser) features not-specific to any particular format backend.
 *<p>
 * NOTE: Jackson 2.x contained these along with JSON-specific features in <code>JsonParser.Feature</code>.
 */
public enum StreamReadFeature
{
    // // // Low-level I/O handling features:

    /**
     * Feature that determines whether parser will automatically
     * close underlying input source that is NOT owned by the
     * parser. If disabled, calling application has to separately
     * close the underlying {@link InputStream} and {@link Reader}
     * instances used to create the parser. If enabled, parser
     * will handle closing, as long as parser itself gets closed:
     * this happens when end-of-input is encountered, or parser
     * is closed by a call to {@link JsonParser#close}.
     *<p>
     * Feature is enabled by default.
     */
    AUTO_CLOSE_SOURCE(true),

    // // // Validity checks
    
    /**
     * Feature that determines whether {@link JsonParser} will explicitly
     * check that no duplicate JSON Object field names are encountered.
     * If enabled, parser will check all names within context and report
     * duplicates by throwing a {@link JsonParseException}; if disabled,
     * parser will not do such checking. Assumption in latter case is
     * that caller takes care of handling duplicates at a higher level:
     * data-binding, for example, has features to specify detection to
     * be done there.
     *<p>
     * Note that enabling this feature will incur performance overhead
     * due to having to store and check additional information: this typically
     * adds 20-30% to execution time for basic parsing.
     */
    STRICT_DUPLICATE_DETECTION(false),

    /**
     * Feature that determines what to do if the underlying data format requires knowledge
     * of all properties to decode (usually via a Schema), and if no definition is
     * found for a property that input content contains.
     * Typically most textual data formats do NOT require schema information (although
     * some do, such as CSV), whereas many binary data formats do require definitions
     * (such as Avro, protobuf), although not all (Smile, CBOR, BSON and MessagePack do not).
     * Further note that some formats that do require schema information will not be able
     * to ignore undefined properties: for example, Avro is fully positional and there is
     * no possibility of undefined data. This leaves formats like Protobuf that have identifiers
     * that may or may not map; and as such Protobuf format does make use of this feature.
     *<p>
     * Note that support for this feature is implemented by individual data format
     * module, if (and only if) it makes sense for the format in question. For JSON,
     * for example, this feature has no effect as properties need not be pre-defined.
     *<p>
     * Feature is disabled by default, meaning that if the underlying data format
     * requires knowledge of all properties to output, attempts to read an unknown
     * property will result in a {@link JsonProcessingException}
     */
    IGNORE_UNDEFINED(false),

    // // // Other

    /**
     * Feature that determines whether {@link JsonLocation} instances should be constructed
     * with reference to source or not. If source reference is included, its type and contents
     * are included when `toString()` method is called (most notably when printing out parse
     * exception with that location information). If feature is disabled, no source reference
     * is passed and source is only indicated as "UNKNOWN".
     *<p>
     * Most common reason for disabling this feature is to avoid leaking
     * internal information; this may be done for security reasons.
     * Note that even if source reference is included, only parts of contents are usually
     * printed, and not the whole contents. Further, many source reference types can not
     * necessarily access contents (like streams), so only type is indicated, not contents.
     *<p>
     * Feature is enabled by default, meaning that "source reference" information is passed
     * and some or all of the source content may be included in {@link JsonLocation} information
     * constructed either when requested explicitly, or when needed for an exception.
     */
    INCLUDE_SOURCE_IN_LOCATION(true),

    ;

    /**
     * Whether feature is enabled or disabled by default.
     */
    private final boolean _defaultState;

    private final int _mask;

    private StreamReadFeature(boolean defaultState) {
        _mask = (1 << ordinal());
        _defaultState = defaultState;
    }

    /**
     * Method that calculates bit set (flags) of all features that
     * are enabled by default.
     */
    public static int collectDefaults()
    {
        int flags = 0;
        for (StreamReadFeature f : values()) {
            if (f.enabledByDefault()) {
                flags |= f.getMask();
            }
        }
        return flags;
    }

    public boolean enabledByDefault() { return _defaultState; }
    public boolean enabledIn(int flags) { return (flags & _mask) != 0; }
    public int getMask() { return _mask; }
}
