# 課題2: Spring MVC を使った ToDo 管理API

ひな形プロジェクトをベースに、ToDo 管理APIを完成させてください。

## 起動方法について

ひな形プロジェクトでは `./gradlew tomcatRun` を実行すれば起動できるように設定されています。

本来は、`./gradlew build` などで War ファイルをビルドし、
それを別にインストールした Tomcat にデプロイすることで起動します。

このひな形プロジェクトでは、その代わりに Gradle の Tomcat-Runner プラグインを使って、
Gradle のタスクとして Tomcat の起動と War ファイルのデプロイを実現しています。


## 仕様

### todo アイテムのリソース形式

 property |  type   | description
----------|---------|------------
 id       | number  | todo アイテムを識別するID
 title    | string  | todo アイテムの内容
 done     | boolean | 消化済みであれば true

### API 仕様

* GET /api/v1/todos
  - 全アイテムを取得する
  - request
    - query: なし
    - body: なし
  - response
    - status code
      - 200: 成功
    - body: `[<todo item>, ...]`

* GET /api/v1/todos/{id}
  - `{id}` で指定したアイテムを取得する
  - request
    - query: なし
    - body: なし
  - response
    - status code
      - 200: 成功
      - 404: 指定したIDを持つアイテムが見つからない場合
    - body: `<todo item>`

* POST /api/v1/todos
  - 指定の内容でアイテムを新規作成 (追加) する
  - request
    - query: なし
    - body: `<todo item>`
      - id は指定不要 (自動で埋められる)
  - response
    - status code
      - 200: 成功
      - 404: 指定したIDを持つアイテムが見つからない場合
    - body: `<todo item>`
      - id が埋まったオブジェクトを返す

* PUT /api/v1/todos/{id}
  - `{id}` で指定したアイテムを、指定の内容で更新する (完全置換)
  - request
    - query: なし
    - body: なし
  - response
    - status code
      - 200: 成功
      - 400: URL path で指定したIDと request body 内のIDが異なる場合
      - 404: 指定したIDを持つアイテムが見つからない場合
    - body: `<todo item>`

* DELETE /api/v1/todos/{id}
  - `{id}` で指定したアイテムを削除する
  - request
    - query: なし
    - body: なし
  - response
    - status code
      - 204: 成功
      - 404: 指定したIDを持つアイテムが見つからない場合
    - body: なし

### 仕様に対するテスト

Python スクリプトからAPIを呼び出し、仕様通りの実装かテストすることができます。

```shell
$ python3 -m venv venv
$ . venv/bin/activate
(venv) $ pip install -r requirements.txt
(venv) $ pytest .
```

テストを実行する前に `./gradlew tomcatRun` などで起動しておきましょう。

2回目以降は `pytest .` のみでOKです。
※プロンプトに `(venv)` がない場合、`. venv/bin/activate` も実行します。
