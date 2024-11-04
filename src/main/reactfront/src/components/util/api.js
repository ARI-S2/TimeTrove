import apiClient from '../../http-commons';


export const fetchProfileImage = async (userCode) => {
    try {
        const response = await apiClient.get(`/user/${userCode}/profile-image`, { responseType: 'blob' });
        if (response.data.size > 0) {
            return URL.createObjectURL(response.data);
        }
        return null;
    } catch (error) {
        console.error('프로필 이미지를 가져오는데 실패했습니다.:', error);
        throw error;
    }
};
