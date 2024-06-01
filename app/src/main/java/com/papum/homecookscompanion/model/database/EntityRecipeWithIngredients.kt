package com.papum.homecookscompanion.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation


@Entity
class EntityRecipeWithIngredients (
    @Embedded
    val recipe: EntityProduct,
    @Relation(
		parentColumn = "id",
		entityColumn = "idRecipe"
	)
    var ingredients: List<EntityProductAndIngredientOf>,

)

