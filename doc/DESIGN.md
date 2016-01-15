# Album
## TrackAlbum
## MultiTrackAlbum
多人运动记录：
role: Participator, // 参与者会记录轨迹
Observer // 观察者不记录轨迹，只观看参与者的轨迹
公开后其他人都可以看到

``` 
Track: {
    name:"",
    description:"",
    start_point:Point,
    end_point:Point,
    
}
``` 
``` 
MultiTrack: {
    Map<User, Track>
}
```
``` 
Category: {
    Type:"Track",
    Name:""
}
``` 

