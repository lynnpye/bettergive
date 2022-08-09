bettergive
==========

Don't you hate having to remember you are looking for somerandommod:torch instead of minecraft:torch and can't use the auto-complete of /give to give yourself that thing?

Yeah... me too; try a better give instead.

This mod adds two commands, one just a shorter alias for the other:
  /bettergive <playertargets> <item> [<count>]
  /bgive <playertargets> <item> [<count>]
  
/bgive operates just like /give, with one exception; the suggested items for tab completion work more intelligently, providing suggestions based on a full string search of all registered items.

So if you start typing e.g.:
  /bgive @s sword
  
You will be presented with a suggestion list of any item whose resource location includes the substring 'sword'. This could be 'minecraft:iron_sword', 'minecraft:stone_sword', or, for our example, 'coolswords:wool_sword' or 'coolswords:slime_sword' (assuming such a mod with such items was installed).

No more having to know beforehand the modid of the mod with the item you want; no more having to type in the modid manually before you can start seeing all of the items matching your search.