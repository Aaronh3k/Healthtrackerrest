package ie.setu.domain

import org.joda.time.DateTime

data class Activity (var id: Int,
                     var userId: Int,
                     var categoryId: Int,
                     var description:String,
                     var duration: Double,
                     var calories: Double,
                     var started: DateTime,
                     var distance: Double,
                     var created_at: DateTime
                     )