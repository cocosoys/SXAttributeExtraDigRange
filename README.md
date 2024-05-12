SX-Attribute插件的属性扩展，提供一个属性:DigRange

提供指令:

/digrange reload 重载配置文件

/digrange testmod 开启调试模式,后台将输出执行过程中的数据

可使得手中物品能够范围破坏方块
配置文件:
```yaml
tool:
  # 允许触发范围破坏的物品
  pickaxe:
    - 278
    - 257
    - 270
    - 274
    - 285
# 允许被范围破坏的方块,支持 xx:xx 写法,例如 16:2
whiteblock:
  pickaxe:
    - "1"
    - "4"
    - "14"
    - "15"
    - "16"
    - "21"
    - "56"
    - "87"
    - "129"
    - "153"
```

若你遇到问题，请输入/digrange testmod，重复一次你所发现的问题，并将后台输出的digrange 数据发送给作者。
