# Status Socket
Status Socket is a plugin that sends player data to a server api. Possible uses are for display on a website, tournaments, or panels. Status socket will send in every tick: playername, run energy, special attack energy, worn equipment, skill data, active prayers, local point, world point, and camera position. Status socket will also send in animation data on player animations and histplat data when hitsplats appear on targets. 

TODO:
- [ ] Proxy Support.
- [ ] Have a tick counter that keeps up with the server in order to connect animation and hitsplat data.
- [ ] 

Player Data Response(every tick): 
```Json
{
  "playerName": "DStat",
  "runEnergy": 100,
  "specialAttack": 1000,
  "equipment": [
    {
      "index": 0,
      "id": 20035,
      "amount": 1
    },
    {
      "index": 1,
      "id": 21295,
      "amount": 1
    },
    {
      "index": 4,
      "id": 20038,
      "amount": 1
    },
    {
      "index": 7,
      "id": 20044,
      "amount": 1
    },
    {
      "index": 9,
      "id": 22981,
      "amount": 1
    },
    {
      "index": 10,
      "id": 22234,
      "amount": 1
    }
  ],
  "inventory": [
    {
      "index": 0,
      "id": 13204,
      "amount": 1505
    },
    {
      "index": 1,
      "id": 12791,
      "amount": 1
    },
    {
      "index": 2,
      "id": 995,
      "amount": 24
    },
    {
      "index": 3,
      "id": 8013,
      "amount": 1942
    }
  ],
  "skills": [
    {
      "skillName": "ATTACK",
      "experience": 14318104,
      "boostedLevel": 99,
      "realLevel": 99
    },
    {
      "skillName": "DEFENCE",
      "experience": 14560208,
      "boostedLevel": 99,
      "realLevel": 99
    },
    {
      "skillName": "STRENGTH",
      "experience": 45480696,
      "boostedLevel": 99,
      "realLevel": 99
    },
    {
      "skillName": "HITPOINTS",
      "experience": 33235204,
      "boostedLevel": 99,
      "realLevel": 99
    },
    {
      "skillName": "RANGED",
      "experience": 15567717,
      "boostedLevel": 99,
      "realLevel": 99
    },
    {
      "skillName": "PRAYER",
      "experience": 13096409,
      "boostedLevel": 73,
      "realLevel": 99
    },
    {
      "skillName": "MAGIC",
      "experience": 17573186,
      "boostedLevel": 99,
      "realLevel": 99
    },
    {
      "skillName": "COOKING",
      "experience": 8511064,
      "boostedLevel": 94,
      "realLevel": 94
    },
    {
      "skillName": "WOODCUTTING",
      "experience": 782722,
      "boostedLevel": 70,
      "realLevel": 70
    },
    {
      "skillName": "FLETCHING",
      "experience": 13071541,
      "boostedLevel": 99,
      "realLevel": 99
    },
    {
      "skillName": "FISHING",
      "experience": 400240,
      "boostedLevel": 63,
      "realLevel": 63
    },
    {
      "skillName": "FIREMAKING",
      "experience": 2836075,
      "boostedLevel": 83,
      "realLevel": 83
    },
    {
      "skillName": "CRAFTING",
      "experience": 3018996,
      "boostedLevel": 84,
      "realLevel": 84
    },
    {
      "skillName": "SMITHING",
      "experience": 803605,
      "boostedLevel": 70,
      "realLevel": 70
    },
    {
      "skillName": "MINING",
      "experience": 787713,
      "boostedLevel": 70,
      "realLevel": 70
    },
    {
      "skillName": "HERBLORE",
      "experience": 6411250,
      "boostedLevel": 91,
      "realLevel": 91
    },
    {
      "skillName": "AGILITY",
      "experience": 865779,
      "boostedLevel": 71,
      "realLevel": 71
    },
    {
      "skillName": "THIEVING",
      "experience": 1329985,
      "boostedLevel": 75,
      "realLevel": 75
    },
    {
      "skillName": "SLAYER",
      "experience": 304584,
      "boostedLevel": 61,
      "realLevel": 61
    },
    {
      "skillName": "FARMING",
      "experience": 13108189,
      "boostedLevel": 99,
      "realLevel": 99
    },
    {
      "skillName": "RUNECRAFT",
      "experience": 266126,
      "boostedLevel": 59,
      "realLevel": 59
    },
    {
      "skillName": "HUNTER",
      "experience": 874205,
      "boostedLevel": 71,
      "realLevel": 71
    },
    {
      "skillName": "CONSTRUCTION",
      "experience": 5372176,
      "boostedLevel": 90,
      "realLevel": 90
    },
    null
  ],
  "prayers": [
    "RAPID_RESTORE",
    "RAPID_HEAL",
    "PROTECT_ITEM",
    "PROTECT_FROM_MISSILES",
    "MYSTIC_MIGHT",
    "PRESERVE"
  ],
  "localPoint": {
    "x": 6464,
    "y": 6208,
    "sceneX": 50,
    "sceneY": 48
  },
  "worldPoint": {
    "x": 3162,
    "y": 3488,
    "plane": 0,
    "regionID": 12598,
    "regionX": 26,
    "regionY": 32
  },
  "camera": {
    "yaw": 0,
    "pitch": 128,
    "x": 6464,
    "y": 5072,
    "z": -847,
    "x2": 6464,
    "y2": -847,
    "z2": 5072
  }
}
```

Animation Data Response(on target animation):
```Json
{"playerName":"DStat","targetName":"Guard","targetId":3010,"attackType":"MELEE","interactionId":419}
```

Hitsplat Data Response(on target hitsplat):
```Json
{"playerName":"DStat","damage":0,"targetName":"Guard","targetId":3010}
```




