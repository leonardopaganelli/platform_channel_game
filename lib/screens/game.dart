import 'package:flutter/src/foundation/key.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:platform_channel_game/constants/colors.dart';
import 'package:platform_channel_game/constants/styles.dart';
import 'package:platform_channel_game/models/creator.dart';

class GameWidget extends StatefulWidget {
  const GameWidget({Key? key}) : super(key: key);

  @override
  State<GameWidget> createState() => _GameWidgetState();
}

class _GameWidgetState extends State<GameWidget> {
  Creator?  creator;
  bool myTurn = false;

  @override
  Widget build(BuildContext context) {
    ScreenUtil.init(context, designSize: const Size(700, 1400));

    return Scaffold(
      body: SingleChildScrollView(
        child: Stack(
          children: [
            Column(
              children: [
                Row(
                  children: [
                    Container(
                      width: ScreenUtil().setWidth(550),
                      height: ScreenUtil().setHeight(550),
                      color: colorLightBlue,
                    ),
                    Container(
                      width: ScreenUtil().setWidth(150),
                      height: ScreenUtil().setHeight(550),
                      color: colorMediumBlue,
                    )
                  ],
                ),
                Container(
                  width: ScreenUtil().setWidth(700),
                  height: ScreenUtil().setHeight(850),
                  color: colorDarkBlue,
                )
              ],
            ),
            Container(
              height: ScreenUtil().setHeight(1400),
              child: Center(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    creator == null
                      ? Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          buildButton("Criar", true),
                          SizedBox(width: 10),
                          buildButton("Entrar", false),
                        ]
                      )
                      : InkWell(
                        child: Text(
                          myTurn == true
                            ? "Faça a sua jogada"
                            : "Aguarde sua vez",
                          style: textStyle36,
                        )
                      )
                  ],
                ),
              ),
            )
          ],
        ),
      ),
    );
  }

  Widget buildButton(String label, bool owner) => Container(
    width: ScreenUtil().setHeight(300),
    child: ElevatedButton(
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: Text(
          label,
          style: textStyle36,
        ),
      ),
      onPressed: () {},
    ),
  );
}