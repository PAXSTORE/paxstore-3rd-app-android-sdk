# ResultCode

| Result Code | Message                      | Description                                                               |
| ----------- | ---------------------------- |---------------------------------------------------------------------------|
| 0           |                              | Success                                                                   |
| 16100       | Sdk param error              | Param error                                                               |
| 16101       | Sdk uninit                   | Sdk uninit                                                                |
| 16102       | Sdk decrypt error            | Decrypt error                                                             |
| 16103       | Sdk json error               | Json error                                                                |
| 16104       | Sdk connect timeout          | Connect timeout, caused by SocketTimeoutException                         |
| 16105       | Sdk unconnect                | Unconnect, caused by ConnectException                                     |
| 16106       | Sdk rquest exception         | Request exception, may caused by IOException, GeneralSecurityException, etc |
| 16107       | Sdk unzip failed             | Unzip zip file failed                                                     |
| 16108       | Sdk md failed                | Download file md5 is wrong                                                |
| 16109       | Sdk replace variables failed | Replace param variables failed                                            |
| 16110       | Sdk init failed              | Init sdk failed                                                           |
| 16111       | Sdk file not found           | File not found, FileNotFoundException occurred                            |
| 16112       | Sdk download ioexception     | IOException occurred during param file download                           |
| 16113       | Sdk download cellular not allowed | Download is not allowed on cellular network (the task is Wi-Fi only)  |
| 16114       | No host available            | No available host to connect                                              |
| 16115       | Api called too fast          | API called too frequently                                                 |
| 16116       | Sdk sha256 failed            | Download file sha256 is wrong                                             |
| 16117       | Sdk sha256 or signature not found | SHA256 or signature not found in the task                            |
| 16118       | Sdk sha256 signature failed  | SHA256 signature verification failed                                      |
| 16119       | Sdk already downloaded       | Params already downloaded, apply result has not been reported yet         |
| 16120       | Param no last success found  | No last successful parameter download task is found                       |
| 16121       | Upload file too large        | Uploaded file is too large                                                |
| 16122       | Upload file remark too long  | Uploaded file remark is too long                                          |
| -1          |                              | Un code                                                                   |
| -10         | No params to download        | No param download task exists in the backend                              |
| 129         | Authentication failed        | Auth failed / missing App context                                         |
| 1016        | App Authentication failed   | App key/secret/signature invalid                       |
| 1010        | App is invalid              | App invalid in market/terminal                         |
| 1201        | App status is invalid       | App status invalid (not ACTIVE/SUSPEND)                      |
| 1800        | Terminal not found          | Terminal SN header missing or not found                |
| 2312        | Terminal Serial No. accept alphanumeric | Terminal SN decrypt failed / invalid format            |
| 1810        | Terminal is inactive        | Terminal inactive                                      |
| 1812        | Terminal is disabled        | Terminal disabled                                      |
| 1952        | Sandbox terminal and app do not belong to one developer | Sandbox terminal and app not same developer            |
| 998         | Bad request                 | Invalid JSON / Content-Type etc.                             |
| 999         | Unknown error               | Unexpected internal error (incl. Kafka)                      |

