import React, { useState, useRef, useEffect } from 'react';
import { useMutation, useQueryClient } from "react-query";
import { fetchProfileImage } from '../util/api';
import apiClient from '../../http-commons';

const CommentItem = ({ comment, userId, profile, nickname, replyInsert, depth }) => {
    const [nestedMsg, setNestedMsg] = useState('');
    const [parentReplyId, setParentReplyId] = useState(null);
    const [parentReplyNickname, setParentReplyNickname] = useState('');
    const [isEditing, setIsEditing] = useState(false);
    const [editContent, setEditContent] = useState(comment.content);
    const [showReplyInput, setShowReplyInput] = useState(false);
    const nestedMsgRef = useRef(null);
    const [profileImageUrl, setProfileImageUrl] = useState(null);
    const queryClient = useQueryClient();

    useEffect(() => {
        if (comment.profileImg.startsWith('http://k.kakaocdn.net/')) {
            setProfileImageUrl(comment.profileImg);
        } else {
            fetchProfileImage(comment.userCode).then(setProfileImageUrl);
        }
    }, [comment.profileImg, comment.userCode]);

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

    const handleReplyClick = (replyId, replyNickname) => {
        setParentReplyId(replyId);
        setParentReplyNickname(replyNickname);
        setShowReplyInput(true);
        if (nestedMsgRef.current) {
            nestedMsgRef.current.focus();
        }
    };

    const handleEditClick = () => {
        setIsEditing(true);
    };

    const handleEditSaveClick = (commentId) => {
        editMutation.mutate({ commentId, content: nestedMsgRef.current.innerHTML });
    };

    const handleDeleteClick = (commentId) => {
        deleteMutation.mutate(commentId);
    };

    const handleReplyInsert = (msg, parentId) => {
        replyInsert(nestedMsgRef.current.innerHTML, parentId);
        setShowReplyInput(false);
        setNestedMsg('');
    };

    const handleNestedMsgChange = () => {
        if (nestedMsgRef.current) {
            setNestedMsg(nestedMsgRef.current.innerText);
        }
    };

    return (
        <div key={comment.id} className="reply-item" style={{ marginLeft: `${depth * 20}px` }}>
            <div className="reply-header">
                <img src={profileImageUrl} alt={comment.nickname} className="reply-profile" />
                <span className="reply-nickname">{comment.nickname}</span>
                <span className="reply-time">{comment.createdAt}</span>
                <div className="reply-actions">
                    <button className="reply-btn" onClick={() => handleReplyClick(comment.id, comment.nickname)}>답글</button>
                    {userId === comment.userCode && (
                        <>
                            <button className="edit-btn" onClick={handleEditClick}>수정</button>
                            <button className="delete-btn" onClick={() => handleDeleteClick(comment.id)}>삭제</button>
                        </>
                    )}
                </div>
            </div>
            <div className="reply-content">
                {isEditing ? (
                    <div
                        className="inputset-textarea form-control"
                        contentEditable
                        placeholder="댓글을 입력하세요"
                        ref={nestedMsgRef}
                        onInput={handleNestedMsgChange}
                        style={{ whiteSpace: 'pre-wrap' }}
                        dangerouslySetInnerHTML={{ __html: editContent }}
                    >
                    </div>
                ) : (
                    <div dangerouslySetInnerHTML={{ __html: comment.content }} />
                )}
            </div>
            {isEditing && (
                <div className="reply-btn">
                    <button className="btnset btnset-round" type="button" style={{borderRadius: "3.5rem"}}
                            onClick={() => handleEditSaveClick(comment.id)}>댓글 수정
                    </button>
                </div>
            )}
            {showReplyInput && parentReplyId === comment.id && (
                <div className="reply-input">
                    <div className="profile-wrapper">
                        <img src={profile} alt="profile" className="profile-image"/>
                        <p className="comment_inbox_name"><b>{nickname}</b></p>
                    </div>
                    <div
                        className="inputset-textarea form-control"
                        contentEditable
                        placeholder="댓글을 입력하세요"
                        ref={nestedMsgRef}
                        onInput={handleNestedMsgChange}
                        style={{ whiteSpace: 'pre-wrap' }}
                    >
                        {comment.id !== comment.parentId && comment.userCode !== userId && (
                            <>
                                <span className="mention-badge">@{parentReplyNickname}</span>
                                <br/><br/>
                            </>
                        )}
                    </div>
                    <div className="reply-btn">
                        <button className="btnset btnset-round" type="button" style={{borderRadius: "3.5rem"}}
                                onClick={() => handleReplyInsert(nestedMsg, comment.id)}>댓글 등록
                        </button>
                    </div>
                </div>
            )}
            {comment.children && comment.children.length > 0 && (
                <div className="children-comments">
                    {comment.children.map(child => (
                        <CommentItem
                            key={child.id}
                            comment={child}
                            userId={userId}
                            profile={profile}
                            nickname={nickname}
                            replyInsert={replyInsert}
                            depth={depth + 1}
                        />
                    ))}
                </div>
            )}
        </div>
    );
};

export default CommentItem;
