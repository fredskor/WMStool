package com.company.Readers;

import java.util.Calendar;

public abstract class AbstractReader {

    public String getPath() {
        return "C:";
    }

    public String getFullPath(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String pathPart = year + "-0" + month + "-0" + day + "_" + hour;

        return String.format("%s\\%s.%s.log", getPath(),getPrefix(),pathPart);

    }

    abstract public String getPrefix();

    public String getTimeFormat() {
        return "HH:mm:ss";
    }
}
