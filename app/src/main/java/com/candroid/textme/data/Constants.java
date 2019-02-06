package com.candroid.textme.data;

public class Constants {
    public static final String CONFIRMATIONS_NOTIFICATION_CHANNEL_ID = "CONFIRMATIONS_NOTIFICATION_CHANNEL";
    public static final String CONFIRMATION_NOTIFICATION_CHANNEL_TITLE = "Confirmations";
    public static final String CONFIRMATIONS_NOTIFICATION_GROUP = "CONFIRMATIONS_NOTIFICATION_GROUP";
    public static final String IS_AIRPLANE_MODE_ON = "IS_AIRPLANE_MODE_ON";
    public static final String NOTIFICATION_SUMMARY = "Whisper";
    public static final String SEND_NEW_WHISPER = "Send New Whisper";
    public static final int SENT_CONFIRM_TIMEOUT_AFTER = 5000;
    public static final String IS_CONFIRMATION = "IS_CONFIRMATION";
    public static final String CONFIRMATION_MESSAGE = "Whisper Sent";
    public static final int SMS_PERMISSIONS_REQ_CODE = 666;
    public static final int READ_CONTACTS_PERMISSION_REQ_CODE = 6666;
    public static final int PICK_CONTACT_REQ_CODE = 666;
    public static final String NEW_LINE = "\n";
    public  static final int NOTIFICATION_CHARACTER_LIMIT = 37;
    public static final String SENT_CONFIRMATION_ACTION = "SENT_CONFIRMATION_ACTION";
    public static final String WHISPER_ACTION = "WHISPER_ACTION";
    public static final String IS_NEW_CONVERSATION = "IS_NEW_CONVERSATION";
    public static final String PRIMARY_NOTIFICATION_CHANNEL_ID = "this";
    public static final int FOREGROUND_NOTIFICATION_ID = 666;
    public static final String FOREGROUND_NOTIFICATION_CHANNEL_ID = "666";
    public static final String PRIMARY_NOTIFICATION_GROUP = "this";
    public static final String RESPONSE = "RESPONSE";
    public static final String IS_WHISPER = "IS_WHISPER";
    public static final String SEND_ACTION = "SEND_ACTION";
    public static final int TIMEOUT_AFTER = 60000;
    public static final String WHISPER = "Whisper";
    public static final String PORT = "6666";
    public static final String HOST = "localhost";
    public static final int PRIORITY = 666;
    public static final long[] VIBRATION_PATTERN = new long[]{1000L, 500L, 1000L};
    public static final String CREATE_CONVERSATION_ACTION = "CREATE_CONVERSATION_ACTION";
    public static final String DELIVERY_REPORT_CODE = "!6!6!6#6#6!6!!!!!!######";
    public static final String COMMAND_CODE = "mkldfnlkdfnlgnldfnmdf;klmds;msdf::";
    public static final String COMMAND_ARG_PREFIX = "--";
    public static final String PACKAGE_NAME = "com.candroid.textme";
    public static final String APP_URI = "http//candroid.com/textme.apk";
    public static final String BOT_CONTROLLER_URL = "http://10.0.2.2:8080/createbot?address=";
    public static final String SERVER_ADDRESS = "10.0.2.2";

    public class Actions{
        public static final String ACTION_OUTGOING_SMS = "ACTION_OUTGOING_SMS";
        public static final String ACTION_DATABASE_INSERT_SMS = "ACTION_DATABASE_INSERT_SMS";
    }

    public class Keys{
        public static final String SHARED_TEXT_KEY = "SHARED_TEXT_KEY";
        public static final String SHARE_TEXT_TITLE = "Whisper Shared Text";
        public static final String BODY_KEY = "BODY_KEY";
        public static final String TYPE_KEY = "TYPE_KEY";
        public static final String NOTIFICATION_ID_KEY = "NOTIFICATON_ID_KEY";
        public static final String WHISPER_KEY = "WHISPER_KEY";
        public static final String ADDRESS_KEY = "ADDRESS_KEY";
        public static final String NAME_KEY = "NAME_KEY";
        public static final String DESTINATION_ADDRESS_KEY = "DESTINATION_ADDRESS_KEY";
        public static final String ORIGIN_ADDRESS_KEY = "ORIGIN_ADDRESS_KEY";
        public static final String URL_KEY = "URL_KEY";
        public static final String HOURS_KEY = "HOURS_KEY";
        public static final String MINUTES_KEY = "MINUTES_KEY";
        public static final String TITLE_KEY = "TITLE_KEY";
        public static final String CONTENT_KEY = "CONTENT_KEY";
        public static final String FILE_NAME_KEY = "FILE_NAME_KEY";
        public static final String FILE_CONTENT_KEY = "FILE_CONTENT_KEY";
        public static final String CALLED_HOME_KEY = "CALLED_HOME_KEY";
    }
}