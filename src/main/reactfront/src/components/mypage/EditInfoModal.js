import React, { useEffect, useState } from 'react';
import { useMutation, useQueryClient } from 'react-query';
import { Button } from '@mui/material';
import apiClient from "../../http-commons";
import { fetchProfileImage } from '../util/api';
import {useUserData} from "../member/authHooks";

function EditInfoModal({ closeModal }) {
    const queryClient = useQueryClient();
    const [profileImage, setProfileImage] = useState(null);
    const [profileImageUrl, setProfileImageUrl] = useState(null);

    const { data: userData, error, isLoading } = useUserData();

    const mutation = useMutation(async (formData) => {
        const response = await apiClient.put('/mypage/user', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return response.data;
    }, {
        onSuccess: () => {
            queryClient.invalidateQueries('userInfo');
            closeModal();
        }
    });

    useEffect(() => {
        if (userData) {
            if (userData.kakaoProfileImg.startsWith('http://k.kakaocdn.net/')) {
                setProfileImageUrl(userData.kakaoProfileImg);
            } else {
                fetchProfileImage(userData.userCode).then(setProfileImageUrl);
            }
        }
    }, [userData]);

    const handleImageChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            setProfileImage(file);
            setProfileImageUrl(URL.createObjectURL(file));
        }
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        if (profileImage) {
            formData.append('profileImage', profileImage);
        }
        mutation.mutate(formData);
    };

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error loading data.</div>;

    return (
        <div>
            <h2>프로필 수정</h2>
            <hr/>
            <form onSubmit={handleSubmit}>
                <div style={{textAlign: 'center', marginTop: '30px'}}>
                    <input
                        type="file"
                        name="profileImage"
                        style={{display: 'none'}}
                        id="profileImage"
                        onChange={handleImageChange}
                    />
                    <label htmlFor="profileImage" style={{cursor: 'pointer'}}>
                        <img
                            src={profileImageUrl}
                            alt="프로필 이미지"
                            style={{
                                width: '100px',
                                height: '100px',
                                borderRadius: '50%',
                                objectFit: 'cover'
                            }}
                        />
                    </label>
                </div>
                <div style={{marginTop: '10px'}}>
                    <label>닉네임: &nbsp; </label>
                    <input type="text" name="kakaoNickname" defaultValue={userData.kakaoNickname}/>
                </div>
                <div style={{marginTop: '30px', textAlign: 'center'}}>
                    <Button
                        variant="contained"
                        color="primary"
                        type="submit"
                        style={{marginRight: '10px'}}
                    >
                        Save
                    </Button>
                    <Button
                        variant="outlined"
                        color="secondary"
                        onClick={closeModal}
                    >
                        Cancel
                    </Button>
                </div>
            </form>
        </div>
    );
}

export default EditInfoModal;
