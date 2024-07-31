import React, { useState, useRef } from 'react';
import { useMutation, useQueryClient } from "react-query";
import apiClient from '../../http-commons';

const Comment = ({ comment, userId, profile, nickname, replyInsert }) => {
    const [nestedMsg, setNestedMsg] = useState('');
    const [parentReplyId, setParentReplyId] = useState(null);
    const [parentReplyNickname, setParentReplyNickname] = useState('');
    const [isEditing, setIsEditing] = useState(false);
    const [editContent, setEditContent] = useState(comment.content);
    const nestedMsgRef = useRef(null);
    const queryClient = useQueryClient();

    const editMutation = useMutation(
        async ({ commentId, content }) => {
            await apiClient.put(`/comment/edit/${commentId}`, { content });
        },
        {
            onSuccess: () => {
                queryClient.invalidateQueries('comments');
                setIsEditing(false);
            },
        }
    );

    const deleteMutation = useMutation(
        async (commentId) => {
            await apiClient.delete(`/comment/delete/${commentId}`);
        },
        {
            onSuccess: () => {
                queryClient.invalidateQueries('comments');
            },
        }
    );

    const handleReplyClick = (replyId, ReplyNickname) => {
        setParentReplyId(replyId);
        setParentReplyNickname(ReplyNickname);
        if (nestedMsgRef.current) {
            nestedMsgRef.current.focus();
        }
    };

    const handleEditClick = (replyId) => {
        setIsEditing(true);
    };
    const handleEditSaveClick = (commentId) => {
        editMutation.mutate({ commentId, content: editContent });
    };

    const handleDeleteClick = (commentId) => {
        deleteMutation.mutate(commentId);
    };

    return (
        <div key={comment.id} className="reply-item" style={{ marginLeft: `${comment.depth * 20}px` }}>
            <div className="reply-header">
                <img src={comment.profileImg} alt={comment.nickname} className="reply-profile" />
                <span className="reply-nickname">{comment.nickname}</span>
                <span className="reply-time">{comment.createdAt}</span>
                <div className="reply-actions">
                    <button className="reply-btn" onClick={() => handleReplyClick(comment.id, comment.nickname)}>답글</button>
                    {userId === comment.userCode && (
                        <>
                            <button className="edit-btn" onClick={() => handleEditClick(comment.id)}>수정</button>
                            <button className="delete-btn" onClick={() => handleDeleteClick(comment.id)}>삭제</button>
                        </>
                    )}
                </div>
            </div>
            <div className="reply-content">
                {isEditing ? (
                    <textarea
                        className="inputset-textarea form-control"
                        placeholder="댓글을 입력하세요"
                        rows="3"
                        value={editContent}
                        onChange={(e) => setEditContent(e.target.value)}                    >
                    </textarea>
                ) : (
                    <p>{comment.content}</p>
                )}
            </div>
            {isEditing && (
                <div className="reply-btn">
                    <button className="btnset btnset-round" type="button" style={{borderRadius: "3.5rem"}}
                     onClick={() => handleEditSaveClick(comment.id)}>댓글 수정
                    </button>
                </div>
            )}
            {parentReplyId === comment.id && (
                <div className="reply-input">
                    <div className="profile-wrapper">
                        <img src={profile} alt="profile" className="profile-image"/>
                        <p className="comment_inbox_name"><b>{nickname}</b></p>
                    </div>
                    <textarea
                        className="inputset-textarea form-control"
                        placeholder="댓글을 입력하세요"
                        rows="3"
                        ref={nestedMsgRef}
                        value={nestedMsg}
                        onChange={(e) => setNestedMsg(e.target.value)}
                    >
                        {comment.id !== comment.parentId && (
                            <span className="mention-badge">@{parentReplyNickname}</span>
                        )}
                    </textarea>

                    <div className="reply-btn">
                        <button className="btnset btnset-round" type="button" style={{borderRadius: "3.5rem"}}
                                onClick={() => replyInsert(nestedMsg, comment.id)}>댓글 등록
                        </button>
                    </div>
                </div>
            )}
            {comment.children && comment.children.length > 0 && (
                <div className="children-comments">
                    {comment.children.map(child => (
                        <Comment
                            key={child.id}
                            comment={child}
                            userId={userId}
                            profile={profile}
                            nickname={nickname}
                            replyInsert={replyInsert}
                        />
                    ))}
                </div>
            )}
        </div>
    );
};

export default Comment;
