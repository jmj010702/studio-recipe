import React, { useState } from 'react';
import './DeleteModal.css';

function DeleteModal({ isOpen, onClose, onConfirm }) {
  const [password, setPassword] = useState('');

  if (!isOpen) return null;

  const handleConfirm = () => {
    if (!password) {
      alert("비밀번호를 입력해주세요.");
      return;
    }
    onConfirm(password); // 부모(MyPage)에게 비밀번호 전달
    setPassword(''); // 입력창 초기화
  };

  const handleClose = () => {
    setPassword('');
    onClose();
  };

  return (
    <div className="modal-overlay" onClick={handleClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>회원 탈퇴</h3>
        <p>탈퇴하시려면 비밀번호를 입력해주세요.<br/>이 작업은 되돌릴 수 없습니다.</p>
        
        <input 
          type="password" 
          className="modal-input"
          placeholder="비밀번호 입력"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleConfirm()} // 엔터키 지원
        />
        
        <div className="modal-actions">
          <button className="modal-btn btn-cancel" onClick={handleClose}>취소</button>
          <button className="modal-btn btn-confirm" onClick={handleConfirm}>탈퇴하기</button>
        </div>
      </div>
    </div>
  );
}

export default DeleteModal;