package info.ankin.projects.git.httpbackend.model;

/**
 * @see <a href=https://www.rfc-editor.org/rfc/rfc3875#section-4.1">RFC 3875 Section 4.1</a>
 * @see <a href="https://www6.uniovi.es/~antonio/ncsa_httpd/cgi/env.html">cgi/env.html</a>
 */
@SuppressWarnings("unused")
public class CGIEnvironmentVariables {

    /**
     * The following environment variables are not request-specific and are
     * set for all requests:
     */
    enum Specification {


        /**
         * The name and version of the information server software answering
         * the request (and running the gateway). Format: name/version
         */
        SERVER_SOFTWARE,

        /**
         * The server's hostname, DNS alias, or IP address as it would appear
         * in self-referencing URLs.
         */
        SERVER_NAME,

        /**
         * The revision of the CGI specification to which this server
         * complies. Format: CGI/revision
         */
        GATEWAY_INTERFACE,
    }

    /**
     * The following environment variables are specific to the request being
     * fulfilled by the gateway program:
     */
    enum Request {

        /**
         * The name and revision of the information protocol this request came
         * in with. Format: protocol/revision
         */
        SERVER_PROTOCOL,

        /**
         * The port number to which the request was sent.
         */
        SERVER_PORT,

        /**
         * The method with which the request was made. For HTTP, this
         * is "GET", "HEAD", "POST", etc.
         */
        REQUEST_METHOD,

        /**
         * The extra path information, as given by the client. In other words,
         * scripts can be accessed by their virtual pathname, followed by
         * extra information at the end of this path. The extra information
         * is sent as PATH_INFO. This information should be decoded by the
         * server if it comes from a URL before it is passed to the CGI
         * script.
         */
        @Required("if GIT_PROJECT_ROOT is set, otherwise PATH_TRANSLATED")
        PATH_INFO,

        /**
         * The server provides a translated version of PATH_INFO, which takes
         * the path and does any virtual-to-physical mapping to it.
         */
        @Required("if GIT_PROJECT_ROOT is set, otherwise PATH_TRANSLATED")
        PATH_TRANSLATED,

        /**
         * A virtual path to the script being executed, used for
         * self-referencing URLs.
         */
        SCRIPT_NAME,

        /**
         * The information which follows the ? in the URL which referenced
         * this script. This is the query information. It should not be
         * decoded in any fashion. This variable should always be set when
         * there is query information, regardless of command line decoding.
         */
        QUERY_STRING,

        /**
         * The hostname making the request. If the server does not have this
         * information, it should set REMOTE_ADDR and leave this unset.
         */
        REMOTE_HOST,

        /**
         * The IP address of the remote host making the request.
         */
        REMOTE_ADDR,

        /**
         * If the server supports user authentication, and the script is
         * protects, this is the protocol-specific authentication method used
         * to validate the user.
         */
        AUTH_TYPE,

        /**
         * If the server supports user authentication, and the script is
         * protected, this is the username they have authenticated as.
         */
        REMOTE_USER,

        /**
         * If the HTTP server supports RFC 931 identification, then this
         * variable will be set to the remote username retrieved from the
         * server. Usage of this variable should be limited to logging only.
         */
        REMOTE_IDENT,

        /**
         * For queries which have attached information, such as HTTP POST and
         * PUT, this is the content type of the data.
         */
        @Required
        CONTENT_TYPE,

        /**
         * The length of the said content as given by the client.
         */
        CONTENT_LENGTH,
    }

    /**
     * In addition to these, the header lines received from the client
     * are placed into the environment with the prefix {@code HTTP_}
     * followed by the header name.
     * <p>
     * Any - characters in the header name are changed to _ characters.
     * The server may exclude any headers which it has already processed,
     * such as {@code Authorization}, {@code Content-type}, and
     * {@code Content-length}.
     * <p>
     * If necessary, the server may choose to exclude any or all of these if
     * including them would exceed any system environment limits.
     * An example of this is the HTTP_ACCEPT variable, defined in CGI/1.0.
     * Another example is the header User-Agent.
     */
    enum Other {

        /**
         * The MIME types which the client will accept, as given by HTTP
         * headers. Other protocols may need to get this
         * information from elsewhere. Each item in this list should be
         * separated by commas as per the HTTP spec.
         * <p>
         * Format:type / subtype, type / subtype
         */
        HTTP_ACCEPT,

        /**
         * The browser the client is using to send the request. General
         * format:software / version library / version.
         */
        HTTP_USER_AGENT,
    }

    public @interface Required {
        String value() default "";
    }
}
