import React from 'react';
import {useMutation, useQuery, useQueryClient} from 'react-query';
import apiClient from "../../http-commons";
import { format, parseISO } from 'date-fns';

function CommentsHistoryModal({ closeModal }) {
    const queryClient = useQueryClient();

    const { data: commentHistory, error, isLoading } = useQuery('commentHistory', async () => {
        const response = await apiClient.get('/mypage/comments');
        console.log(response.data);
        return response.data;
    });

    const deleteCommentMutation = useMutation(
        async (commentId) => {
            await apiClient.delete(`/comment/delete/${commentId}`);
        },
        {
            onSuccess: () => {
                queryClient.invalidateQueries('commentHistory');
            },
        }
    );

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error loading data.</div>;

    const groupCommentsByDate = (comments) => {
        return comments.reduce((groups, comment) => {
            const date = format(parseISO(comment.createdAt), 'yyyy-MM-dd');
            if (!groups[date]) {
                groups[date] = [];
            }
            groups[date].push(comment);
            return groups;
        }, {});
    };

    // 유틸리티 함수로 댓글을 날짜별로 그룹화
    const groupedComments = groupCommentsByDate(commentHistory);

    const handleDeleteComment = (commentId) => {
        deleteCommentMutation.mutate(commentId);
    };

    return (
        <div className="comments-history-modal">
            <button className="btn-close" onClick={closeModal}>닫기</button>
            <h2 className="contents-tit" style={{ marginBottom: '20px' }}>내 TimeTrove 댓글</h2>
            {Object.keys(groupedComments).map(date => (
                <div key={date} className="comments-group">
                    <h3>{date}</h3>
                    <hr />
                    {groupedComments[date].map(comment => (
                        <div key={comment.id} className="comment" style={{ position: 'relative' }}>
                            <button className="delete-btn"
                                onClick={() => handleDeleteComment(comment.id)}
                            >
                                x
                            </button>
                            <p className="badgeset badgeset-fill badgeset-border badgeset-round">{format(parseISO(comment.createdAt), 'HH : mm : ss')}</p>
                            <p className="comment-content">{comment.content}</p>
                            <p>
                                <a href={`/board/detail/${comment.no}`} target="_blank" rel="noopener noreferrer">
                                    {comment.subject}
                                </a>에 남긴 댓글
                            </p>
                        </div>
                    ))}
                </div>
            ))}
        </div>
    );
}

export default CommentsHistoryModal;
