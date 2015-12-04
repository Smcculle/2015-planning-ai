namespace Planning
{
    /**
     * Project settings
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class Settings
    {

        /** Major version number */
        public static readonly int MAJOR_VERSION = 1;

        /** Minor version number */
        public static readonly int MINOR_VERSION = 0;

        /** A UID number constructed from the major and minor version numbers */
        //public static readonly long VERSION_UID = ByteBuffer.allocate(8).putInt(MAJOR_VERSION).putInt(MINOR_VERSION).getLong(0);

        /** The name of the supertype of all types */
        public static readonly string DEFAULT_TYPE = "object";
}
}