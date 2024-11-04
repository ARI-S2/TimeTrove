import React, { useState, useEffect, useRef } from 'react';
import { useMutation, useQuery, useQueryClient } from "react-query";
import apiClient from '../../http-commons';
import CommentItem from './CommentItem'; // CommentItem 컴포넌트 import

const CommentContainer = ({ boardNo, userId, profile, nickname }) => {
    const [msg, setMsg] = useState('');
    const [comments, setComments] = useState([]);
    const msgRef = useRef(null);

    const { data: commentsData, refetch: commentsRefetch } = useQuery(['comments', boardNo],
        async () => {
            const response = await apiClient.get(`/comment/${boardNo}`);
            return response.data;
        }
    );

    const mutation = useMutation(async (newReply) => {
        const response = await apiClient.post('/comment/add', newReply);
        return response.data;
    });

    useEffect(() => {
        commentsRefetch();
    }, [boardNo]);

    useEffect(() => {
        if (commentsData) {
            setComments(commentsData);
        }
    }, [commentsData]);


    const replyInsert = (reply, parentId = null) => {
        if (reply.trim() === "") {
            if (parentId) {
                msgRef.current.focus();
            }
            return;
        }
        mutation.mutate({
            no: boardNo,
            userCode: userId,
            content: reply,
            parentId: parentId
        }, {
            onSuccess: (newReply) => {
                setMsg('');
                if (parentId) {
                    setComments(comments => comments.map(comment =>
                        comment.id === parentId
                            ? {...comment, children: [...comment.children, newReply]}
                            : comment
                    ));
                } else {
                    setComments([...comments, newReply]);
                }
            }
        });
    };

    return (
        <div className="form-wrap">
            <h3 className="form-tit">
                <span></span>댓글
            </h3>
            <div className="reply-list">
                {comments && comments.map(comment => (
                    <CommentItem
                        key={comment.id}
                        comment={comment}
                        userId={userId}
                        profile={profile}
                        nickname={nickname}
                        replyInsert={replyInsert}
                        depth={0}
                    />
                ))}
            </div>

            {userId ? (
                <div>
                    <div className="inputset inputset-round">
                        <div className="profile-wrapper">
                            <img src={profile} alt="profile" className="profile-image"/>
                            <p className="comment_inbox_name"><b>{nickname}</b></p>
                        </div>
                        <textarea className="inputset-textarea form-control" placeholder="댓글을 입력하세요"
                                  style={{marginTop: "0.5em"}}
                                  rows="3"
                                  ref={msgRef} value={msg}
                                  onChange={(e) => setMsg(e.target.value)}
                        ></textarea>
                    </div>
                    <div className="reply-btn" style={{marginTop: "0.5em"}}>
                        <button className="btnset btnset-round" type="button"
                                style={{borderRadius: "3.5rem"}}
                                onClick={() => replyInsert(msg)}>
                            댓글 등록
                        </button>
                    </div>
                </div>
            ) : (
                <span>로그인시 댓글 작성이 가능합니다</span>
            )}
        </div>
    );
};

export default CommentContainer;
