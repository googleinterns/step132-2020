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

import com.google.sps.data.TimeRange;
import com.google.cloud.tasks.v2.AppEngineHttpRequest;
import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;
import com.google.common.base.Strings;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import java.util.Calendar;
import java.nio.charset.Charset;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

/**
 * Task to send a reminder email to the user once the tutoring session ends
 */
public final class RatingEmailTask {
    private long scheduledSeconds;

    public RatingEmailTask(TimeRange timeslot) throws IOException {
        int year = 2020;
        int month = 6;
        int day = 22;
        int hour = 10;
        int min = 0;

        Calendar scheduledDate = Calendar.getInstance();
        scheduledDate.set(Calendar.YEAR, year);
        scheduledDate.set(Calendar.MONTH, month);
        scheduledDate.set(Calendar.DAY_OF_MONTH, day);
        scheduledDate.set(Calendar.HOUR_OF_DAY, hour);
        scheduledDate.set(Calendar.MINUTE, min);
        this.scheduledSeconds = scheduledDate.getTime().getTime();
        scheduleTask();
    }

    public void scheduleTask() throws IOException {
        // Instantiates a client.
        try (CloudTasksClient client = CloudTasksClient.create()) {
            
            String projectId = "icecube-step-2020";
            String queueName = "rating-email-queue";
            String location = "us-central1";
            String payload = "test";

            // Construct the fully qualified queue name.
            String queuePath = QueueName.of(projectId, location, queueName).toString();

            // Construct the task body.
            Task.Builder taskBuilder =
                Task.newBuilder()
                    .setAppEngineHttpRequest(
                        AppEngineHttpRequest.newBuilder()
                            .setBody(ByteString.copyFrom(payload, Charset.defaultCharset()))
                            .setRelativeUri("/rating-email")
                            .setHttpMethod(HttpMethod.POST)
                            .build());

            System.out.println(this.scheduledSeconds);

            // Add the scheduled time to the request.
            int seconds = 10;
            taskBuilder.setScheduleTime(
                Timestamp.newBuilder()
                    .setSeconds(Instant.now(Clock.systemUTC()).plusSeconds(seconds).getEpochSecond()));

            // Send create task request.
            Task task = client.createTask(queuePath, taskBuilder.build());
            System.out.println("Task created: " + task.getName());
        }
        // [END cloud_tasks_appengine_create_task]
    }
}


