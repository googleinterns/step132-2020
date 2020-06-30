// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

import java.util.Calendar;

public final class TimeRange {
    
    private int start;
    private int duration;
    private int end;
    private Calendar date;

    private TimeRange(int start, int duration, Calendar date) {
        this.start = start;
        this.duration = duration;
        this.end = start + duration;
        this.date = date;
    }

    /** Create new TimeRange with start time (in minutes) and end time (in minutes). */
    public static TimeRange fromStartToEnd(int start, int end, Calendar date) {
        return new TimeRange(start, end - start, date);
    }

    /** Create new TimeRange with start time (in minutes) and duration (in minutes). */
    public static TimeRange fromStartWithDuration(int start, int duration, Calendar date) {
        return new TimeRange(start, duration, date);
    }

    public int getStart() {
        return this.start;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getEnd() {
        return this.end;
    }

    public Calendar getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return getMinutesInTime(start) + "-" + getMinutesInTime(end);
    }

    /** Converts hour:minutes time to minutes. */
    public static int getTimeInMinutes(int hours, int minutes) {
        if (hours < 0 || hours >= 24) {
            throw new IllegalArgumentException("Hours can only be 0 through 23 (inclusive).");
        }

        if (minutes < 0 || minutes >= 60) {
            throw new IllegalArgumentException("Minutes can only be 0 through 59 (inclusive).");
        }

        return (hours * 60) + minutes;
    }

    /** Converts minute times to hour:minutes time. */
    private String getMinutesInTime(int time) {
        String hour = String.valueOf(time / 60);
        String minutes = String.valueOf(time % 60);

        if(minutes.length() < 2) {
            minutes = "0" + minutes;
        }

        return hour + ":" + minutes;

    }

}
