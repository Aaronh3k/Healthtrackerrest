package ie.setu.domain

import org.joda.time.DateTime

data class Category (var id: Int,
                 var name: String,
                 var description: String,
                 var created_at: DateTime)