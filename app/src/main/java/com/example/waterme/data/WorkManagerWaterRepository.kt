/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.waterme.data

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.waterme.model.Plant
import com.example.waterme.worker.WaterReminderWorker
import java.util.concurrent.TimeUnit

class WorkManagerWaterRepository(context: Context) : WaterRepository {
    private val workManager = WorkManager.getInstance(context)

    override val plants: List<Plant>
        get() = DataSource.plants // Gets plant data from DataSource

    override fun scheduleReminder(duration: Long, unit: TimeUnit, plantName: String) {
        // Creates a data obj for passing plant name to the worker
        val data = Data.Builder()
        data.putString(WaterReminderWorker.nameKey, plantName)

        // Builds one time work request for the background task WaterReminderWorker
        val workRequestBuilder = OneTimeWorkRequestBuilder<WaterReminderWorker>()
            .setInitialDelay(duration, unit) // Delays by user chosen time
            .setInputData(data.build()) // Passes data to worker
            .build()

        // Enqueues the work request
        workManager.enqueueUniqueWork(
            plantName + duration, // Gives a unique work name
            ExistingWorkPolicy.REPLACE, // Replaces existing work if the unique name is the same
            workRequestBuilder // Executes work request
        )
    }
}