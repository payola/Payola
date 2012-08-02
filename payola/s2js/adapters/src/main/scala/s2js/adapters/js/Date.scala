package s2js.adapters.js

class Date
{
    /**
     * Returns the day of the month (from 1-31).
     */
    def getDate: Int = 0

    /**
     * Returns the day of the week (from 0-6).
     */
    def getDay: Int = 0

    /**
     * Returns the year (four digits)
     */
    def getFullYear: Int = 0

    /**
     * Returns the hour (from 0-23)
     */
    def getHours: Int = 0

    /**
     * Returns the milliseconds (from 0-999)
     */
    def getMilliseconds: Int = 0

    /**
     * Returns the minutes (from 0-59)
     */
    def getMinutes: Int = 0

    /**
     * Returns the month (from 0-11)
     */
    def getMonth: Int = 0

    /**
     * Returns the seconds (from 0-59)
     * @return
     */
    def getSeconds: Int = 0

    /**
     * Returns the number of milliseconds since midnight Jan 1, 1970
     */
    def getTime: Double = 0

    /**
     * Returns the time difference between GMT and local time, in minutes
     */
    def getTimezoneOffset: Int = 0

    /**
     * Returns the day of the month, according to universal time (from 1-31)
     */
    def getUTCDate: Int = 0

    /**
     * Returns the day of the week, according to universal time (from 0-6)
     */
    def getUTCDay: Int = 0

    /**
     * Returns the year, according to universal time (four digits)
     */
    def getUTCFullYear: Int = 0

    /**
     * Returns the hour, according to universal time (from 0-23)
     */
    def getUTCHours: Int = 0

    /**
     * Returns the milliseconds, according to universal time (from 0-999)
     */
    def getUTCMilliseconds: Int = 0

    /**
     * Returns the minutes, according to universal time (from 0-59)
     */
    def getUTCMinutes: Int = 0

    /**
     * Returns the month, according to universal time (from 0-11)
     */
    def getUTCMonth: Int = 0

    /**
     * Returns the seconds, according to universal time (from 0-59)
     */
    def getUTCSeconds: Int = 0

    /**
     * Parses a date string and returns the number of milliseconds since midnight of January 1, 1970
     */
    def parse(date: String): Double = 0

    /**
     * Sets the day of the month of a date object
     */
    def setDate(date: Int) {}

    /**
     * Sets the year (four digits) of a date object
     */
    def setFullYear(date: Int) {}

    /**
     * Sets the hour of a date object
     */
    def setHours(hour: Int) {}

    /**
     * Sets the milliseconds of a date object
     */
    def setMilliseconds(mili: Int) {}

    /**
     * Set the minutes of a date object
     */
    def setMinutes(minutes: Int) {}

    /**
     * Sets the month of a date object
     */
    def setMonth(month: Int) {}

    /**
     * Sets the seconds of a date object
     */
    def setSeconds(seconds: Int) {}

    /**
     * Sets a date and time by adding or subtracting a specified number of milliseconds to/from midnight January 1, 1970
     */
    def setTime(time: Double) {}

    /**
     * Sets the day of the month of a date object, according to universal time
     */
    def setUTCDate(time: Double) {}

    /**
     * Sets the year of a date object, according to universal time (four digits)
     */
    def setUTCFullYear(year: Int) {}

    /**
     * Sets the hour of a date object, according to universal time
     */
    def setUTCHours(hour: Int) {}

    /**
     * Sets the milliseconds of a date object, according to universal time
     */
    def setUTCMilliseconds(milis: Int) {}

    /**
     * Set the minutes of a date object, according to universal time
     */
    def setUTCMinutes(minutes: Int) {}

    /**
     * Sets the month of a date object, according to universal time
     */
    def setUTCMonth(month: Int) {}

    /**
     * Set the seconds of a date object, according to universal time
     */
    def setUTCSeconds(seconds: Int) {}

    /**
     * Converts the date portion of a Date object into a readable string
     */
    def toDateString: String = ""

    /**
     * Deprecated. Use the toUTCString() method instead
     */
    def toGMTString: String = ""

    /**
     * Returns the date as a string, using the ISO standard
     */
    def toISOString: String = ""

    /**
     * Returns the date as a string, formated as a JSON date
     */
    def toJSON: String = ""

    /**
     * Returns the date portion of a Date object as a string, using locale conventions
     */
    def toLocaleDateString: String = ""

    /**
     * Returns the time portion of a Date object as a string, using locale conventions
     */
    def toLocaleTimeString: String = ""

    /**
     * Converts a Date object to a string, using locale conventions
     */
    def toLocaleString: String = ""

    /**
     * Converts a Date object to a string
     */
    override def toString: String = ""

    /**
     * Converts the time portion of a Date object to a string
     */
    def toTimeString: String = ""

    /**
     * Converts a Date object to a string, according to universal time
     */
    def toUTCString: String = ""

    /**
     * Returns the primitive value of a Date object
     */
    def valueOf(): Double = 0
}
