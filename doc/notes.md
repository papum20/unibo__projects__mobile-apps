# NOTES

## Can I use...?
(to ask)  

*	external libraries
	*	`‘com.google.android.material:material:1.2.0’` for `com.google.android.material.bottomnavigation.BottomNavigationView`

## db thoughts

Current problem:
1.	a product is identified by the whole hierarchy of its categories (so you could think to use name as primary key)
2.	...but name can't contain it all because you wouldn't be able to query (e.g. search al food of type meat) -> so you could think to use, as key, name (sub-name) + parent (recursive)... 
3.	but, in this way, a recipe could be child of non-edible (e.g. soap) (or, at least, it wouldn't be enforced by the db itself, but you'd have to be careful)
4.	and you can't neither use 3 different tables (recipes, edible, non-edible), because inventory and list can contain any product
5.	on second thought, take example of bechamel, which could be either recipe or bought, and both should be in same category... so checking manually the parent could be ok
