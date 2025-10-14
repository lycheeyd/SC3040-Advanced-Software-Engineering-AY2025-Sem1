enum ActionType {
  SIGN_UP,
  FORGOT_PASSWORD,
  DELETE_ACCOUNT,
  SEND_NEW_PASSWORD,
}

extension ActionTypeExtension on ActionType {
  String get value {
    switch (this) {
      case ActionType.SIGN_UP:
        return "SIGN_UP";
      case ActionType.FORGOT_PASSWORD:
        return "FORGOT_PASSWORD";
      case ActionType.DELETE_ACCOUNT:
        return "DELETE_ACCOUNT";
      case ActionType.SEND_NEW_PASSWORD:
        return "SEND_NEW_PASSWORD";
    }
  }
}
