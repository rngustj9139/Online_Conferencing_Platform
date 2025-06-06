package koo.online_education_platform.service.chatService;

import koo.online_education_platform.dto.ChatRoomDto;
import koo.online_education_platform.dto.ChatRoomMap;
import koo.online_education_platform.service.fileService.FileService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MsgChatService와 RtcChatService 기능의 공동된 부분을 모아놓은 Service 계층
 */
@Service
@Getter @Setter
@RequiredArgsConstructor
@Slf4j
public class ChatServiceMain {

    private final MsgChatService msgChatService;
    private final RtcChatService rtcChatService;
    private final FileService fileService;

    // 전체 채팅방 조회
    public List<ChatRoomDto> findAllRoom () {
        // 채팅방 생성 순서를 최근순으로 반환
        List<ChatRoomDto> chatRooms = new ArrayList<>(ChatRoomMap.getInstance().getChatRooms().values());
        Collections.reverse(chatRooms);

        return chatRooms;
    }

    // roomID 기준으로 채팅방 찾기
    public ChatRoomDto findRoomById (String roomId) {
        return ChatRoomMap.getInstance().getChatRooms().get(roomId);
    }

    // roomName으로 채팅방 만들기
    public ChatRoomDto createChatRoom (String roomName, String roomPwd, boolean secretChk, int maxUserCnt, String chatType) {
        ChatRoomDto room;

        // 채팅방 타입에 따라서 사용되는 Service 구분
        if (chatType.equals("msgChat")) {
            room = msgChatService.createChatRoom(roomName, roomPwd, secretChk, maxUserCnt);
        } else {
            room = rtcChatService.createChatRoom(roomName, roomPwd, secretChk, maxUserCnt);
        }

        return room;
    }

    // 채팅방 비밀번호 조회
    public boolean confirmPwd (String roomId, String roomPwd) {
//        String pwd = chatRoomMap.get(roomId).getRoomPwd();

        return roomPwd.equals(ChatRoomMap.getInstance().getChatRooms().get(roomId).getRoomPwd());
    }

    // 채팅방 인원 + 1
    public void plusUserCnt (String roomId) {
        log.info("cnt = {}", ChatRoomMap.getInstance().getChatRooms().get(roomId).getUserCount());

        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);
        room.setUserCount(room.getUserCount() + 1);
    }

    // 채팅방 인원 - 1
    public void minusUserCnt (String roomId) {
        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);
        room.setUserCount(room.getUserCount() - 1);
    }

    // maxUserCnt에 따른 채팅방 입장 여부
    public boolean chkRoomUserCnt (String roomId) {
        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);

        if (room.getUserCount() + 1 > room.getMaxUserCnt()) {
            return false;
        }

        return true;
    }

    // 채팅방 삭제
    public void delChatRoom (String roomId) {
        try {
            // 채팅방 타입에 따라서 단순히 채팅방만 삭제할지 업로드된 파일도 삭제할지 결정
            if (ChatRoomMap.getInstance().getChatRooms().get(roomId).getChatType().equals(ChatRoomDto.ChatType.MSG)) { // MSG 채팅방은 사진도 추가 삭제
                // 채팅방 내부에 업로드 된 파일 삭제
                fileService.deleteFileDir(roomId);
            }

            ChatRoomMap.getInstance().getChatRooms().remove(roomId);

            log.info("삭제 완료 roomId : {}", roomId);
        } catch (Exception e) { // 예외 발생시 확인을 위한 try catch
            log.error(e.getMessage());
        }
    }

}
