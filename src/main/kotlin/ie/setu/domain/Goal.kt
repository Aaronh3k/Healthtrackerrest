package ie.setu.domain

import org.joda.time.DateTime

data class Goal (var id: Int,
                 var userId: Int,
                 var calories: Int,
                 var standing_hours: Float,
                 var steps: Int,
                 var distance: Int,
                 var created_at: DateTime)