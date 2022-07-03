package com.funnco.fcmessenger.common.model

class ChatModel {
    var chatId: String? = null
    var chatMembers: List<UserModel>? = null
    var chatName: String? = null
    var avatarFilepath: String? = null
    var lastMessage: MessageModel? = null
    var isChatPrivate: Boolean? = null
}