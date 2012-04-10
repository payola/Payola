package s2js.adapters.js.dom

class Date
{
    /**
     * Returns the day of the month (from 1-31).
     * @return
     */
    def getDate(): Int = 0

    /**
     * Returns the day of the week (from 0-6).
     * @return
     */
    def getDay(): Int = 0

    /**
     * Returns the year (four digits)
     * @return
     */
    def getFullYear(): Int = 0

    /**
     * Returns the hour (from 0-23)
     * @return
     */
    def getHours(): Int = 0

    /**
     * Returns the milliseconds (from 0-999)
     * @return
     */
    def getMilliseconds(): Int = 0

    /**
     * Returns the minutes (from 0-59)
     * @return
     */
    def getMinutes(): Int = 0

    /**
     * Returns the month (from 0-11)
     * @return
     */
    def getMonth(): Int = 0

    /**
     * Returns the seconds (from 0-59)
     * @return
     */
    def getSeconds(): Int = 0

    /**
     * Returns the number of milliseconds since midnight Jan 1, 1970
     * @return
     */
    def getTime(): Double = 0

    /**
     * Returns the time difference between GMT and local time, in minutes
     * @return
     */
    def getTimezoneOffset(): Int = 0

    /**
     * Returns the day of the month, according to universal time (from 1-31)
     * @return
     */
    def getUTCDate(): Int = 0

    /**
     * Returns the day of the week, according to universal time (from 0-6)
     * @return
     */
    def getUTCDay(): Int = 0

    /**
     * Returns the year, according to universal time (four digits)
     * @return
     */
    def getUTCFullYear(): Int = 0

    /**
     * Returns the hour, according to universal time (from 0-23)
     * @return
     */
    def getUTCHours(): Int = 0

    /**
     * Returns the milliseconds, according to universal time (from 0-999)
     * @return
     */
    def getUTCMilliseconds(): Int = 0

    /**
     * Returns the minutes, according to universal time (from 0-59)
     * @return
     */
    def getUTCMinutes(): Int = 0

    /**
     * Returns the month, according to universal time (from 0-11)
     * @return
     */
    def getUTCMonth(): Int = 0

    /**
     * Returns the seconds, according to universal time (from 0-59)
     * @return
     */
    def getUTCSeconds(): Int = 0

    /**
     * Parses a date string and returns the number of milliseconds since midnight of January 1, 1970
     * @return
     */
    def parse(date: String): Double = 0

    /**
     * Sets the day of the month of a date object
     * @param date
     */
    def setDate(date: Int) = {}

    /**
     * Sets the year (four digits) of a date object
     * @param date
     */
    def setFullYear(date: Int) = {}

    /**
     * Sets the hour of a date object
     * @param hour
     */
    def setHours(hour: Int) = {}

    /**
     * Sets the milliseconds of a date object
     * @param mili
     */
    def setMilliseconds(mili: Int) = {}

    /**
     * Set the minutes of a date object
     * @param minutes
     */
    def setMinutes(minutes: Int) = {}

    /**
     * Sets the month of a date object
     * @param month
     */
    def setMonth(month: Int) = {}

    /**
     * Sets the seconds of a date object
     * @param seconds
     */
    def setSeconds(seconds: Int) = {}

    /**
     * Sets a date and time by adding or subtracting a specified number of milliseconds to/from midnight January 1, 1970
     * @param time
     */
    def setTime(time: Double) = {}

    /**
     * Sets the day of the month of a date object, according to universal time
     * @param time
     */
    def setUTCDate(time: Double) = {}

    /**
     * Sets the year of a date object, according to universal time (four digits)
     * @param year
     */
    def setUTCFullYear(year: Int) = {}

    /**
     * Sets the hour of a date object, according to universal time
     * @param hour
     */
    def setUTCHours(hour: Int) = {}

    /**
     * Sets the milliseconds of a date object, according to universal time
     * @param milis
     */
    def setUTCMilliseconds(milis: Int) = {}

    /**
     * Set the minutes of a date object, according to universal time
     * @param minutes
     */
    def setUTCMinutes(minutes: Int) = {}

    /**
     * Sets the month of a date object, according to universal time
     * @param month
     */
    def setUTCMonth(month: Int) = {}

    /**
     * Set the seconds of a date object, according to universal time
     * @param seconds
     */
    def setUTCSeconds(seconds: Int) = {}

    /**
     * Converts the date portion of a Date object into a readable string
     * @return
     */
    def toDateString(): String = ""

    /**
     * Deprecated. Use the toUTCString() method instead
     * @return
     */
    def toGMTString(): String = ""

    /**
     * Returns the date as a string, using the ISO standard
     * @return
     */
    def toISOString(): String = ""

    /**
     * Returns the date as a string, formated as a JSON date
     * @return
     */
    def toJSON(): String = ""

    /**
     * Returns the date portion of a Date object as a string, using locale conventions
     * @return
     */
    def toLocaleDateString(): String = ""

    /**
     * Returns the time portion of a Date object as a string, using locale conventions
     * @return
     */
    def toLocaleTimeString(): String = ""

    /**
     * Converts a Date object to a string, using locale conventions
     * @return
     */
    def toLocaleString(): String = ""

    /**
     * Converts a Date object to a string
     * @return
     */
    override def toString(): String = ""

    /**
     * Converts the time portion of a Date object to a string
     * @return
     */
    def toTimeString(): String = ""

    /**
     * Converts a Date object to a string, according to universal time
     * @return
     */
    def toUTCString(): String = ""

    /**
     * Returns the primitive value of a Date object
     * @return
     */
    def valueOf(): Double = 0
}
