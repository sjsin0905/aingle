export interface CharacterInfoShort {
  characterId: number;
  name: string;
  job: string;
  age: number;
  tone: boolean;
  personality: string;
  talkType: boolean;
  etc: any;
  imageUrl: string;
}

export interface CharacterInfo {
  characterId: number;
  name: string;
  job: string;
  age: number;
  tone: string;
  talkType: string;
  personality: string;
  imageUrl: string;
  summary: string;
}

export interface ICharInfo {
  name: string;
  onClose: () => void;
  userName: string;
  age: number;
  profileImg: string;
  job: string;
  personality: string;
  tone: string;
  talkType: string;
}
